package gov.epa.eis.batch.xmlsnapshot.writer;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DDelVecc on 5/29/2018.
 */
public class CERSXmlHeaderCallback implements StaxWriterCallback {

    private String userIdentifier;
    private String programSystemCode;
    private Integer emissionsYear;
    private String model;
    private String modelVersion;
    private String emissionsCreationDate;
    private String submittalComment;

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getProgramSystemCode() {
        return programSystemCode;
    }

    public void setProgramSystemCode(String programSystemCode) {
        this.programSystemCode = programSystemCode;
    }

    public Integer getEmissionsYear() {
        return emissionsYear;
    }

    public void setEmissionsYear(Integer emissionsYear) {
        this.emissionsYear = emissionsYear;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getEmissionsCreationDate() {
        return emissionsCreationDate;
    }

    public void setEmissionsCreationDate(String emissionsCreationDate) {
        this.emissionsCreationDate = emissionsCreationDate;
    }

    public String getSubmittalComment() {
        return submittalComment;
    }

    public void setSubmittalComment(String submittalComment) {
        this.submittalComment = submittalComment;
    }
//    <cer:UserIdentifier>Beth</cer:UserIdentifier>
//    <cer:ProgramSystemCode>NCDAQ</cer:ProgramSystemCode>
//    <cer:EmissionsYear>2008</cer:EmissionsYear>
//    <cer:Model>None</cer:Model>
//    <cer:SubmittalComment>Some very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long comment</cer:SubmittalComment>


    @Override
    public void write(XMLEventWriter writer) throws IOException {
        try{
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();

            createNode(writer, "cer", "UserIdentifier", userIdentifier);
            createNode(writer, "cer", "ProgramSystemCode", programSystemCode);
            if (emissionsYear != null)
                createNode(writer, "cer", "EmissionsYear", emissionsYear.toString());
            createNode(writer, "cer", "Model", model);
            if (StringUtils.isNotBlank(modelVersion))
                createNode(writer, "cer", "ModelVersion", modelVersion);
            if (StringUtils.isNotBlank(emissionsCreationDate))
                createNode(writer, "cer", "EmissionsCreationDate", emissionsCreationDate);
            createNode(writer, "cer", "SubmittalComment", submittalComment);


        } catch (XMLStreamException e){
            System.err.println("Something went nuts!!!");
        }
    }

    private static void createNode(XMLEventWriter eventWriter, String prefix, String element,
                                   String value) throws XMLStreamException {
        XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        //Create Start node
        StartElement sElement = xmlEventFactory.createStartElement(prefix, "", element);
        eventWriter.add(sElement);
        //Create Content
        Characters characters = xmlEventFactory.createCharacters(value);
        eventWriter.add(characters);
        // Create End node
        EndElement eElement = xmlEventFactory.createEndElement(prefix, "", element);
        eventWriter.add(eElement);

    }
}
