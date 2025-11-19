package gov.epa.eis.web.webService;

import gov.epa.eis.model.EtlStatus;
import gov.epa.eis.model.XmlSubmission;
import gov.epa.eis.service.SubmissionService;
import gov.epa.eis.web.EisGeneralController;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/webService")
public class WebServiceController extends EisGeneralController {

    @Autowired
    private SubmissionService submissionService;

    @RequestMapping(value = "/xmlFeedback", method = RequestMethod.GET)
    public ResponseEntity<String> xmlFeedback(@RequestParam("transactionId") String transactionId,
                                              HttpSession session,
                                              HttpServletRequest request) {

        String response = null;
//		Map<String,String[]> parameters = request.getParameterMap();

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        httpHeaders.setCacheControl("private,must-revalidate");
        httpHeaders.setPragma("private");

        //Missing Txn Id
        if (StringUtils.isBlank(transactionId)) {
            response = "CDX transaction ID has not been provided.";
            return new ResponseEntity<String>(response, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        XmlSubmission xmlSubmission = submissionService.getXmlSubmission(transactionId);


        if (xmlSubmission != null) {
            if (!(xmlSubmission.getEtlStatus().equals(EtlStatus.COMPLETED) || xmlSubmission.getEtlStatus().equals(EtlStatus.FAILED)
                    || xmlSubmission.getEtlStatus().equals(EtlStatus.PARTIAL))) {
                if (xmlSubmission.getEtlStatus().equals(EtlStatus.NEW)) {
                    response = "Submitted file has not finished processing:  File is currently processing.";
                } else if (xmlSubmission.getEtlStatus().equals(EtlStatus.PURGED)) {
                    response = "QA Submission that has been purged:  Feedback report is no longer available.";
                } else {
                    response = "There was a problem processing the XML Submission.  ETL Status = " + xmlSubmission.getEtlStatus();
                }
                return new ResponseEntity<String>(response, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response = "CDX transaction ID is invalid.";
            return new ResponseEntity<String>(response, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final byte[] rawReport = xmlSubmission.getRawReport();
        if (rawReport != null) {
            InputStream fin = null;
            BufferedInputStream in = null;
            ByteArrayOutputStream bos = null;
            GzipCompressorInputStream gzIn = null;
            try {
                fin = new ByteArrayInputStream(rawReport);
                in = new BufferedInputStream(fin);
                bos = new ByteArrayOutputStream();
                gzIn = new GzipCompressorInputStream(in);
                final byte[] buffer = new byte[2048];
                int n = 0;
                while (-1 != (n = gzIn.read(buffer, 0, buffer.length))) {
                    bos.write(buffer, 0, n);
                }
                httpHeaders.setContentType(MediaType.APPLICATION_XML);
                return new ResponseEntity<String>(new String(bos.toByteArray()), httpHeaders, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (gzIn != null) {
                        gzIn.close();
                    }
                    if (fin != null) {
                        fin.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
