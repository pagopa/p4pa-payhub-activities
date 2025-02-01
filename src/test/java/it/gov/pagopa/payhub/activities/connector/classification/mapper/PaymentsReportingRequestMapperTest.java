package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReportingRequestBody;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentsReportingRequestMapperTest {

    private final PaymentsReportingRequestMapper mapper = Mappers.getMapper(PaymentsReportingRequestMapper.class);

    @Test
    void testMap() {
        // Given
        PaymentsReporting paymentsReporting = PaymentsReportingFaker.buildPaymentsReporting();


        // When
        PaymentsReportingRequestBody result = mapper.map(paymentsReporting);

        // Then
        TestUtils.reflectionEqualsByName(paymentsReporting, result);
        TestUtils.checkNotNullFields(result,"creationDate","updateDate","updateOperatorExternalId","pspIdentifier");
    }

    @Test
    void testMapWithNull() {
        // When
        PaymentsReportingRequestBody result = mapper.map(null);

        // Then
        assertNull(result);
    }
}