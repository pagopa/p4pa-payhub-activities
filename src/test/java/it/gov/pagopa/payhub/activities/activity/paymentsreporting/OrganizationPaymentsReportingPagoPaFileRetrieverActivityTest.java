package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationPaymentsReportingPagoPaFileRetrieverActivityTest {
	private OrganizationPaymentsReportingPagoPaFileRetrieverActivity activity;

	@BeforeEach
	void setUp() {
		activity = new OrganizationPaymentsReportingPagoPaFileRetrieverActivityImpl();
	}

	@Test
	void getPaymentsReportingFile() {
		// Given
		List<PaymentsReportingIdDTO> paymentsReportingIds = List.of();

		// When Then
		assertDoesNotThrow(() -> activity.getPaymentsReportingFile(paymentsReportingIds));
	}
}