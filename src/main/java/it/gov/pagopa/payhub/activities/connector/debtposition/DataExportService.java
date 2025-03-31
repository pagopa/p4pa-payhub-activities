package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;

import java.util.List;

/**
 * This interface provides methods that manage Data export of debt positions within the related microservice
 */
public interface DataExportService {
    /**
     * Export paid installments based on the provided request parameters.
     *
     * @param organizationId the ID of the organization (required)
     * @param operatorExternalUserId the external user ID of the operator (required)
     * @param paidExportFileFilter the filter containing the request parameters (required)
     * @param page zero-based page index (0..N) (optional, default to 0)
     * @param size the size of the page to be returned (optional, default to 20)
     * @param sort sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported. (optional)
     * @return PagedInstallmentsPaidView
     */
    PagedInstallmentsPaidView exportPaidInstallments(Long organizationId, String operatorExternalUserId, PaidExportFileFilter paidExportFileFilter, Integer page, Integer size, List<String> sort);
}

