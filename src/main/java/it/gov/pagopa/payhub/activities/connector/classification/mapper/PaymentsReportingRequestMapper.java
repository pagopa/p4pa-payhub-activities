package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReportingRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentsReportingRequestMapper {
    PaymentsReportingRequestBody map(PaymentsReporting paymentsReporting);
}
