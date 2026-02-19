package it.gov.pagopa.payhub.activities.service.ingestionflow.assessments;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Service
public class AssessmentsErrorArchiverService extends
        ErrorArchiverService<AssessmentsErrorDTO, AssessmentsIngestionFlowFileResult> {

    protected AssessmentsErrorArchiverService(@Value("${folders.shared}") String sharedFolder,
                                              @Value("${folders.process-target-sub-folders.errors}") String errorFolder,
                                              FileArchiverService fileArchiverService,
                                              CsvService csvService) {
        super(sharedFolder, errorFolder, fileArchiverService, csvService);
    }

    @Override
    protected List<String[]> getHeaders(AssessmentsIngestionFlowFileResult result) {
        return Collections.singletonList(
                new String[]{"File Name", "Row Number", "Assessment code", "Organization IPA code", "Error Code", "Error Message"});
    }
}
