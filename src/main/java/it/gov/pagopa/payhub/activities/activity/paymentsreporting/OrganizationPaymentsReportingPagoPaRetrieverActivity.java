package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import java.util.List;

public interface OrganizationPaymentsReportingPagoPaRetrieverActivity {
	List<Long> retrieve(Long organizationId);
}
