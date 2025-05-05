package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.pu.classification.dto.generated.PagedClassificationView;
import it.gov.pagopa.pu.classification.dto.generated.PagedFullClassificationView;
import it.gov.pagopa.pu.processexecutions.dto.generated.ClassificationsExportFileFilter;

import java.util.List;

/**
 * This interface provides methods that manage Data export of classifications within the related microservice
 */
public interface DataExportService {

    PagedClassificationView exportClassificationView(Long organizationId, String operatorExternalUserId, ClassificationsExportFileFilter classificationsExportFileFilter, Integer page, Integer size, List<String> sort);
    PagedFullClassificationView exportFullClassificationView(Long organizationId, String operatorExternalUserId, ClassificationsExportFileFilter classificationsExportFileFilter, Integer page, Integer size, List<String> sort);
}
