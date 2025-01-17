package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReportingRequestBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentsReportingRequestMapperTest {

    @Test
    void testMap() {
        // Given
        PaymentsReporting paymentsReporting = PaymentsReportingFaker.buildPaymentsReporting();


        // When
        PaymentsReportingRequestBody result = PaymentsReportingRequestMapper.map(paymentsReporting);

        // Then
        assertEquals(1L, result.getOrganizationId());
        assertEquals("IUV", result.getIuv());
        assertEquals("IUR", result.getIur());
        assertEquals("IUF", result.getIuf());
        assertEquals(100L, result.getTotalAmountCents());
        assertEquals(1, result.getTransferIndex());

    }

    @Test
    void testMapWithNull() {
        // When
        PaymentsReportingRequestBody result = PaymentsReportingRequestMapper.map(null);

        // Then
        assertNull(result);
    }
}