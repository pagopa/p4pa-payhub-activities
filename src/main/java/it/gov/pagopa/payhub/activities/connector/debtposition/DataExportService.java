package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * This interface provides methods that manage Data export of debt positions within the related microservice
 */
public interface DataExportService {
    /**
     *
     * @param organizationId The ID of the organization (required)
     * @param operatorExternalUserId  (required)
     * @param paymentDateFrom  (required)
     * @param paymentDateTo  (required)
     * @param debtPositionTypeOrgId  (optional)
     * @param page Zero-based page index (0..N) (optional, default to 0)
     * @param size The size of the page to be returned (optional, default to 20)
     * @param sort Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported. (optional)
     * @return PagedInstallmentsPaidView
     */
    PagedInstallmentsPaidView exportPaidInstallments(Long organizationId, String operatorExternalUserId, OffsetDateTime paymentDateFrom, OffsetDateTime paymentDateTo, Long debtPositionTypeOrgId, Integer page, Integer size, List<String> sort);
}

