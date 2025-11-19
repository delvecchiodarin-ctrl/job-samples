package gov.epa.eis.batch.xmlsnapshot.config;

import gov.epa.eis.batch.cers.XEvent;
import gov.epa.eis.batch.cers.XFacilitySite;
import gov.epa.eis.batch.cers.XLocation;
import gov.epa.eis.batch.xmlsnapshot.writer.CERSXmlHeaderCallback;
import gov.epa.eis.batch.xmlsnapshot.listener.XmlSnapshotJobListener;
import gov.epa.eis.batch.xmlsnapshot.processor.AreaSnapshotItemProcessor;
import gov.epa.eis.batch.xmlsnapshot.processor.EventSnapshotItemProcessor;
import gov.epa.eis.batch.xmlsnapshot.processor.FacilitySnapshotItemProcessor;
import gov.epa.eis.batch.xmlsnapshot.processor.PointEmissionSnapshotItemProcessor;
import gov.epa.eis.batch.xmlsnapshot.reader.SnapshotHibernateCursorItemReader;
import gov.epa.eis.batch.xmlsnapshot.writer.SnapshotStaxEventItemWriter;
import gov.epa.eis.model.DataCategory;
import gov.epa.eis.model.Event;
import gov.epa.eis.model.FacilitySite;
import gov.epa.eis.model.dto.LocationDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class XmlSnapshotJobConfiguration {
    private static final Log LOG = LogFactory.getLog(XmlSnapshotJobConfiguration.class);
    public static final String JOB_NAME = "XML-SNAPSHOT";
    private static final DataCategory EVENT = DataCategory.EVENT;
    public static final String EVENT_SNAPSHOT_HQL_QUERY_STRING = "select e "
            + " from Event e"
            + " join fetch e.reportingPeriods rp"
            + " join fetch rp.eventLocations l"
            + " join fetch l.eventProcesses ep"
            + " left join fetch ep.eventEmissions em"
            + " where ep.id in ("
            + " select d.processId "
            + " from ReportRequest r "
            + " join r.details d "
            + " where r.id = :reportRequestId"
            + " )"
            + " order by e.id, rp.id, l.id, "
            + " ep.id, em.id ";

    public static final String AREA_SNAPSHOT_HQL_QUERY_STRING = "select l "
            + " from LocationDto l"
            + " join fetch l.emissionsProcesses ep"
            + " join fetch ep.reportingPeriods rp"
//            + " left join fetch rp.supplementalParameters sp"
            + " join fetch rp.emissions em"
//            + " left join fetch rp.excludedLocations el"
//            + " left join fetch ep.regulations r"
            + " left join fetch ep.controlApproaches ca"
            + " left join fetch ca.controlMeasures cm"
            + " left join fetch ca.controlPollutants cp"
            + " where rp.dataSetIdentifier.id = :dataSetId"
            + " and ep.id in ("
            + " select d.processId "
            + " from ReportRequest r "
            + " join r.details d "
            + " where r.id = :reportRequestId"
            + " )"
            + " order by l.id, ep.id, rp.id, coalesce(em.id,0),"    //, coalesce(sp.id,0),"
//                        + " coalesce(em.id,0), coalesce(el.id,0), coalesce(r.id,0)";
            + " coalesce(ca.id,0), coalesce(cm.id,0), coalesce(cp.id,0) "; //, coalesce(cm.id,0), coalesce(cp.id,0)

    public static final String POINT_EMISSION_SNAPSHOT_HQL_QUERY_STRING = "select f "
            + " from FacilitySite f"
//            + " left join f.alternateIdentifiers fai with fai.programSystemCode.code = :programSystemCode "
            + " join fetch f.units u"
//            + " left join u.alternateIdentifiers uai with uai.programSystemCode.code = :programSystemCode "
            + " join fetch u.processes ep"
            + " join fetch ep.reportingPeriods rp"
            + " join fetch rp.emissions em"
            + " where rp.dataSetIdentifier.id = :dataSetId"
            + " and ep.id in ("
            + " select d.processId "
            + " from ReportRequest r "
            + " join r.details d "
            + " where r.id = :reportRequestId"
            + " ) "
            + " order by f.id, u.id, ep.id, rp.id, "
            + " coalesce(em.id,0) ";

    public static final String FACILITY_SNAPSHOT_HQL_QUERY_STRING = "select f "
            + " from FacilitySite f"
//            + " left join f.alternateIdentifiers fai with fai.programSystemCode.code = :programSystemCode "
//            + " left join fetch f.releasePoints p"
            + " left join fetch f.units u"
//            + " left join u.alternateIdentifiers uai with uai.programSystemCode.code = :programSystemCode "
            + " left join fetch u.processes ep"
//            + " left join fetch u.controlApproaches uca"
//            + " left join fetch uca.controlMeasures ucm"
//            + " left join fetch uca.controlPollutants ucp"
//            + " left join fetch ep.controlApproaches pca"
//            + " left join fetch pca.controlMeasures pcm"
//            + " left join fetch pca.controlPollutants pcp"
            + " where f.id in ("
            + " select d.facilityId "
            + " from ReportRequest r "
            + " join r.details d "
            + " where r.id = :reportRequestId"
            + " ) "
            + " order by f.id, u.id, ep.id";//, p.id ";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Step eventSnapshotStep;

    @Autowired
    private Step areaSnapshotStep;

    @Autowired
    private Step pointEmissionSnapshotStep;

    @Autowired
    private Step facilitySnapshotStep;

    @Autowired
    private XmlSnapshotJobListener xmlSnapshotJobListener;

    @Bean(name="eventSnapshotStep")
    public Step eventSnapshotStep(@Qualifier("eventXmlItemWriter") SnapshotStaxEventItemWriter<XEvent> writer,
                                  @Qualifier("eventItemReader") SnapshotHibernateCursorItemReader<Event> reader) {
        return stepBuilderFactory.get("eventSnapshotStep").<Event, XEvent> chunk(40)
                .reader(reader)
                .processor(processXmlSnapshot())
                .writer(writer)
                .build();
    }

    @Bean(name ="eventSnapshotJob")
    public Job eventSnapshotJob() {
        return jobBuilderFactory.get("eventSnapshotJob")
                .incrementer(new RunIdIncrementer())
                .flow(eventSnapshotStep)
                .end()
                .listener(xmlSnapshotJobListener)
                .build();
    }

    @Bean(name="areaSnapshotStep")
    public Step areaSnapshotStep(@Qualifier("areaXmlItemWriter") SnapshotStaxEventItemWriter<XLocation> writer,
                                 @Qualifier("areaItemReader") SnapshotHibernateCursorItemReader<LocationDto> reader,
                                 @Qualifier("areaXmlItemProcessor") ItemProcessor<LocationDto, XLocation> itemProcessor) {
//        AreaSnapshotItemProcessor itemProcessor = (AreaSnapshotItemProcessor) areaXmlItemProcessor();
        //itemProcessor.setReportRequestId(reader.getReportRequestId());
//        itemProcessor.setReportRequestId(reportRequestId);
        return stepBuilderFactory.get("areaSnapshotStep").<LocationDto, XLocation> chunk(6)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean(name="areaSnapshotJob")
    public Job areaSnapshotJob() {
        return jobBuilderFactory.get("areaSnapshotJob")
                .incrementer(new RunIdIncrementer())
                .flow(areaSnapshotStep)
                .end()
                .listener(xmlSnapshotJobListener)
                .build();
    }


    @Bean(name="pointEmissionSnapshotStep")
    public Step pointEmissionSnapshotStep(@Qualifier("pointEmissionXmlItemWriter") SnapshotStaxEventItemWriter<XFacilitySite> writer,
                                          @Qualifier("pointEmissionItemReader") SnapshotHibernateCursorItemReader<FacilitySite> reader,
                                          @Qualifier("pointEmissionXmlItemProcessor") ItemProcessor<FacilitySite, XFacilitySite> itemProcessor) {
        return stepBuilderFactory.get("pointEmissionSnapshotStep").<FacilitySite, XFacilitySite> chunk(40)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
    @Bean(name="pointEmissionSnapshotJob")
    public Job pointEmissionSnapshotJob() {
        return jobBuilderFactory.get("pointEmissionSnapshotStep")
                .incrementer(new RunIdIncrementer())
                .flow(pointEmissionSnapshotStep)
                .end()
                .listener(xmlSnapshotJobListener)
                .build();
    }

    @Bean(name="facilitySnapshotStep")
    public Step facilitySnapshotStep(@Qualifier("facilityXmlItemWriter") SnapshotStaxEventItemWriter<XFacilitySite> writer,
                                     @Qualifier("facItemReader") SnapshotHibernateCursorItemReader<FacilitySite> reader,
                                     @Qualifier("facilityXmlItemProcessor") ItemProcessor<FacilitySite, XFacilitySite> itemProcessor) {
        return stepBuilderFactory.get("facilitySnapshotStep").<FacilitySite, XFacilitySite> chunk(10)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
    @Bean(name="facilitySnapshotJob")
    public Job facilitySnapshotJob() {
        return jobBuilderFactory.get("facilitySnapshotStep")
                .incrementer(new RunIdIncrementer())
                .flow(facilitySnapshotStep)
                .end()
                .listener(xmlSnapshotJobListener)
                .build();
    }

    @Bean
    public ItemProcessor<Event, XEvent> processXmlSnapshot(){
        return new EventSnapshotItemProcessor();
    }

    @Bean
    public ItemProcessor<LocationDto, XLocation> areaXmlItemProcessor(){
        return new AreaSnapshotItemProcessor();
    }

    @Bean(name="pointEmissionXmlItemProcessor")
    @StepScope
    public static ItemProcessor<FacilitySite, XFacilitySite> pointEmissionXmlItemProcessor(@Value("#{jobParameters[ProgramSystemCode]}") String programSystemCode){
        PointEmissionSnapshotItemProcessor itemProcessor = new PointEmissionSnapshotItemProcessor();
        itemProcessor.setProgramSystemCode(programSystemCode);
        return itemProcessor;
    }

    @Bean(name="facilityXmlItemProcessor")
    @StepScope
    public static ItemProcessor<FacilitySite, XFacilitySite> facilityXmlItemProcessor(@Value("#{jobParameters[ProgramSystemCode]}") String programSystemCode){
        FacilitySnapshotItemProcessor itemProcessor = new FacilitySnapshotItemProcessor();
        itemProcessor.setProgramSystemCode(programSystemCode);
        return itemProcessor;
    }

    @Bean(destroyMethod = "")
    @StepScope
    public static SnapshotStaxEventItemWriter<XEvent> eventXmlItemWriter(@Qualifier("cersXmlHeaderCallback") CERSXmlHeaderCallback cersXmlHeaderCallback,
                                                           @Value("#{jobParameters[ReportRequestOutputFilePath]}") String reportRequestOutputFilePath) throws IOException {
        return new SnapshotStaxEventItemWriter<>(XEvent.class, cersXmlHeaderCallback, reportRequestOutputFilePath);
    }

    @Bean
    @StepScope
    SnapshotHibernateCursorItemReader<Event> eventItemReader(@Value("#{jobParameters[ReportRequestId]}") Long reportRequestId) {
        SnapshotHibernateCursorItemReader<Event> itemReader = new SnapshotHibernateCursorItemReader<>(reportRequestId, EVENT_SNAPSHOT_HQL_QUERY_STRING);
        itemReader.setSessionFactory(sessionFactory);
        return itemReader;
    }

    @Bean(destroyMethod = "")
    @StepScope
    public static SnapshotStaxEventItemWriter<XLocation> areaXmlItemWriter(@Qualifier("cersXmlHeaderCallback") CERSXmlHeaderCallback cersXmlHeaderCallback,
                                                             @Value("#{jobParameters[ReportRequestOutputFilePath]}") String reportRequestOutputFilePath) throws IOException {
        return new SnapshotStaxEventItemWriter<>(XLocation.class, cersXmlHeaderCallback, reportRequestOutputFilePath);
    }

    @Bean
    @StepScope
    SnapshotHibernateCursorItemReader<LocationDto> areaItemReader(@Value("#{jobParameters[ReportRequestId]}") Long reportRequestId, @Value("#{jobParameters[dataSetId]}") Long dataSetId) {
        SnapshotHibernateCursorItemReader<LocationDto> itemReader = new SnapshotHibernateCursorItemReader<>(reportRequestId, dataSetId, AREA_SNAPSHOT_HQL_QUERY_STRING);
        itemReader.setSessionFactory(sessionFactory);
        return itemReader;
    }

    @Bean(name="pointEmissionXmlItemWriter", destroyMethod = "")
    @StepScope
    public static SnapshotStaxEventItemWriter<XFacilitySite> pointEmissionXmlItemWriter(@Qualifier("cersXmlHeaderCallback") CERSXmlHeaderCallback cersXmlHeaderCallback,
                                                                          @Value("#{jobParameters[ReportRequestOutputFilePath]}") String reportRequestOutputFilePath) throws IOException {
        return new SnapshotStaxEventItemWriter<>(XFacilitySite.class, cersXmlHeaderCallback, reportRequestOutputFilePath);
    }

    @Bean(name="pointEmissionItemReader")
    @StepScope
    SnapshotHibernateCursorItemReader<FacilitySite> pointEmissionItemReader(@Value("#{jobParameters[ReportRequestId]}") Long reportRequestId, @Value("#{jobParameters[dataSetId]}") Long dataSetId) {
        SnapshotHibernateCursorItemReader<FacilitySite> itemReader = new SnapshotHibernateCursorItemReader<>(reportRequestId, dataSetId, POINT_EMISSION_SNAPSHOT_HQL_QUERY_STRING);
        itemReader.setSessionFactory(sessionFactory);
        return itemReader;
    }

    @Bean(name="facilityXmlItemWriter", destroyMethod = "")
    @StepScope
    public static SnapshotStaxEventItemWriter<XFacilitySite> facilityXmlItemWriter(@Qualifier("cersXmlHeaderCallback") CERSXmlHeaderCallback cersXmlHeaderCallback,
                                                                     @Value("#{jobParameters[ReportRequestOutputFilePath]}") String reportRequestOutputFilePath) throws IOException {
        return new SnapshotStaxEventItemWriter<>(XFacilitySite.class, cersXmlHeaderCallback, reportRequestOutputFilePath);
    }

    @Bean(name="facItemReader")
    @StepScope
    SnapshotHibernateCursorItemReader<FacilitySite> facilityItemReader(@Value("#{jobParameters[ReportRequestId]}") Long reportRequestId) {
        SnapshotHibernateCursorItemReader<FacilitySite> itemReader = new SnapshotHibernateCursorItemReader<>(reportRequestId, FACILITY_SNAPSHOT_HQL_QUERY_STRING);
        itemReader.setSessionFactory(sessionFactory);
        return itemReader;
    }

    @Bean(name="cersXmlHeaderCallback")
    @StepScope
    public static CERSXmlHeaderCallback cersXmlHeaderCallback(@Value("#{jobParameters[InventoryYear]}") String inventoryYear, @Value("#{jobParameters[ProgramSystemCode]}") String programSystemCode, @Value("#{jobParameters[UserIdentifier]}") String userIdentifier) throws IOException {
        //Setup CERS Header XML Information
        CERSXmlHeaderCallback headerCallback = new CERSXmlHeaderCallback();
        headerCallback.setUserIdentifier(userIdentifier);
        headerCallback.setProgramSystemCode(programSystemCode);
        headerCallback.setEmissionsYear(StringUtils.isNotBlank(inventoryYear) ? Integer.parseInt(inventoryYear) : null);
        return headerCallback;
    }

}