package it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentOptionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {InstallmentDTOMapper.class})
public interface PaymentOptionDTOMapper {

  PaymentOptionDTO map(it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO paymentOptionDTO);
}
