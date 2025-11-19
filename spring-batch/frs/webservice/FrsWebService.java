package gov.epa.eis.batch.frs.webservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



@Component
public class FrsWebService {
	 private static final Log LOG = LogFactory.getLog(FrsWebService.class);
    @Autowired
    private FrsRestTemplate frsRestTemplate;
    
   

    public FrsWebService() {
        //
    }

    public BulkUploadResponse bulkUpload(final File zipFile) {

        BulkUploadResponse bulkUploadResponse = null;

        HttpHeaders httpHeaders = new HttpHeaders();
        //specify MULTIPART_FORM_DATA so we can add file to request body
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        Resource file = new FileSystemResource(zipFile) {
            @Override
            public String getFilename(){
                return "eis_frs_bulkupload.zip";
            }
        };
        HttpHeaders zipHeaders = new HttpHeaders();
        zipHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<Resource> zipEntity = new HttpEntity<Resource>(file, zipHeaders);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = getRequestEntity(httpHeaders);

        //add file to request body
        requestEntity.getBody().add("file", zipEntity);
     
        // If an exception is thrown during a POST call, it is caught here
               
        ResponseEntity<BulkUploadResponse> response = null;
		try {

			response = frsRestTemplate.postForEntity(frsRestTemplate.BULK_UPLOAD_URL,
					requestEntity, BulkUploadResponse.class);
			bulkUploadResponse = response.getBody();
			loadRequestResponsePayload(bulkUploadResponse);
			LOG.info("Transaction Id returned by FRS:" + bulkUploadResponse.getTransactionId());

		}

		catch (Exception e) {
			LOG.info("Error Processing FRS webservice POST call", e);
            loadRequestResponsePayload(bulkUploadResponse = new BulkUploadResponse());
            throw e;
		} finally  {
		return bulkUploadResponse;
        }

    }

    public BulkUploadStatusResponse getBulkUploadStatus(final String transactionId) {

        BulkUploadStatusResponse bulkUploadStatusResponse = null;
        
        LOG.info("FrsWebService: Make a GET call using the TX id");

        ResponseEntity<BulkUploadStatusResponse> response = frsRestTemplate.exchange(frsRestTemplate.BULK_UPLOAD_STATUS_URL, HttpMethod.GET,
                getRequestEntity(new HttpHeaders()), BulkUploadStatusResponse.class,
                transactionId);

        bulkUploadStatusResponse = response.getBody();

        loadRequestResponsePayload(bulkUploadStatusResponse);

        return bulkUploadStatusResponse;
    }

    private HttpEntity<MultiValueMap<String, Object>> getRequestEntity(HttpHeaders httpHeaders) {
        return new HttpEntity<>(new LinkedMultiValueMap<String, Object>(), httpHeaders);
    }

    private void loadRequestResponsePayload(final FrsResponse frsResponse) {
        RequestResponseLoggingInterceptor requestResponseLoggingInterceptor = (RequestResponseLoggingInterceptor)frsRestTemplate.getInterceptors().stream().filter(i -> i.getClass().equals(RequestResponseLoggingInterceptor.class)).findFirst().get();
        frsResponse.setFrsRequestResponsePayload(requestResponseLoggingInterceptor.getFrsRequestResponsePayload());
    }
    
	
}
