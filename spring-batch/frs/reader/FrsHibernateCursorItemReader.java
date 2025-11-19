package gov.epa.eis.batch.frs.reader;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DDelVecc on 5/8/2019.
 */
public class FrsHibernateCursorItemReader<T> extends HibernateCursorItemReader<T> implements StepExecutionListener {
    private final Class<T> persistentClass;
    protected Date lastPullDate;

    @Autowired
    public FrsHibernateCursorItemReader(SessionFactory sessionFactory, final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        this.setUseStatelessSession(false);

        String hqlQueryString = "select e "
                + " from " + persistentClass.getSimpleName() + " e"
                + " where e.lastUpdatedDate > :lastPullDate ";

        this.setQueryString(hqlQueryString);
        this.setSessionFactory(sessionFactory);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        lastPullDate = (Date)stepExecution.getJobParameters().getParameters().get("lastPullDate").getValue();
        //set report request id to target...
        Map<String, Object> parameterValues = new HashMap<>();
        if (lastPullDate != null) {
            parameterValues.put("lastPullDate", lastPullDate);
        }
        
     // get the last pull date an dpass it to the criteria hibernate query
     //   so make sure you update some facility
        this.setParameterValues(parameterValues);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        int readCount = stepExecution.getReadCount();
        
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        Long overallReadCount = (Long) executionContext.get("readCount");
        if (overallReadCount == null)
        {overallReadCount = 0L;
        executionContext.putLong("readCount", readCount + overallReadCount);
        }
        return stepExecution.getExitStatus();
    }

}