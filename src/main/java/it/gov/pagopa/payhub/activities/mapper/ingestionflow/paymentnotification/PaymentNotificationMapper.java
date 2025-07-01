package it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.PersonDTO;
import it.gov.pagopa.pu.classification.dto.generated.PersonEntityType;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.stereotype.Service;

@Service
public class PaymentNotificationMapper {

  public PaymentNotificationDTO map(PaymentNotificationIngestionFlowFileDTO dto, IngestionFlowFile ingestionFlowFile) {

    PaymentNotificationDTO paymentNotificationDTO = new PaymentNotificationDTO();
    paymentNotificationDTO.setOrganizationId(ingestionFlowFile.getOrganizationId());
    paymentNotificationDTO.setIngestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId());
    paymentNotificationDTO.setIud(dto.getIud());
    paymentNotificationDTO.setIuv(dto.getIuv());
    paymentNotificationDTO.setPaymentExecutionDate(dto.getPaymentExecutionDate());
    paymentNotificationDTO.setPaymentType(dto.getPaymentType());
    paymentNotificationDTO.setAmountPaidCents(dto.getAmountPaidCents().longValue());
    paymentNotificationDTO.setPaCommissionCents(dto.getPaCommissionCents().longValue());
    paymentNotificationDTO.setRemittanceInformation(dto.getRemittanceInformation());
    paymentNotificationDTO.setTransferCategory(dto.getTransferCategory());
    paymentNotificationDTO.setDebtPositionTypeOrgCode(dto.getDebtPositionTypeOrgCode());
    paymentNotificationDTO.setBalance(dto.getBalance());
    paymentNotificationDTO.setDebtor(mapPersonalDataFromPaymentNotification(dto));


    return paymentNotificationDTO;
  }


  public static PersonDTO mapPersonalDataFromPaymentNotification(PaymentNotificationIngestionFlowFileDTO dto) {
    return PersonDTO.builder()
        .entityType(PersonEntityType.valueOf((dto.getDebtorUniqueIdentifierType())))
        .fiscalCode(dto.getDebtorUniqueIdentifierCode())
        .fullName(dto.getDebtorFullName())
        .address(dto.getDebtorAddress())
        .civic(dto.getDebtorCivic())
        .postalCode(dto.getDebtorPostalCode())
        .location(dto.getDebtorLocation())
        .province(dto.getDebtorProvince())
        .nation(dto.getDebtorNation())
        .email(dto.getDebtorEmail())
        .build();
  }

}


