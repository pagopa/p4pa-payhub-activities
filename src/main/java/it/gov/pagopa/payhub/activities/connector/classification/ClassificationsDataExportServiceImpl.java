package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.ClassificationsDataExportClient;
import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class ClassificationsDataExportServiceImpl implements ClassificationsDataExportService {

    private final ClassificationsDataExportClient classificationsDataExportClient;
    private final AuthnService authnService;

    public ClassificationsDataExportServiceImpl(ClassificationsDataExportClient classificationsDataExportClient, AuthnService authnService) {
        this.classificationsDataExportClient = classificationsDataExportClient;
        this.authnService = authnService;
    }

    @Override
    public PagedClassificationView exportClassificationView(Long organizationId, String operatorExternalUserId, ClassificationsExportFileFilter classificationsExportFileFilter, Integer page, Integer size, List<String> sort) {
        return classificationsDataExportClient.getPagedClassificationView(authnService.getAccessToken(), organizationId, operatorExternalUserId, classificationsExportFileFilter, page, size, sort);
    }

    @Override
    public PagedFullClassificationView exportFullClassificationView(Long organizationId, String operatorExternalUserId, ClassificationsExportFileFilter classificationsExportFileFilter, Integer page, Integer size, List<String> sort) {
        return classificationsDataExportClient.getPagedFullClassificationView(authnService.getAccessToken(), organizationId, operatorExternalUserId, classificationsExportFileFilter, page, size, sort);
    }
}
