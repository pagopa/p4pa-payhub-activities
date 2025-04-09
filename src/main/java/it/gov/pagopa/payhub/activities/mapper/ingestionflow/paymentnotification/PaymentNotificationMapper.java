package it.gov.pagopa.payhub.activities.mapper.ingestionflow.paymentnotification;

import io.nexusrpc.Service;
import it.gov.pagopa.payhub.activities.dto.paymentnotification.PaymentNotificationIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import it.gov.pagopa.pu.classification.dto.generated.Person;
import it.gov.pagopa.pu.classification.dto.generated.PersonEntityType;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;

@Service
public class PaymentNotificationMapper {

  public PaymentNotificationDTO map(PaymentNotificationIngestionFlowFileDTO dto, IngestionFlowFile ingestionFlowFile) {

    return PaymentNotificationDTO.builder()
        .organizationId(ingestionFlowFile.getOrganizationId())
        .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
        .iud(dto.getIud())
        .iuv(dto.getIuv())
        .paymentExecutionDate(dto.getPaymentExecutionDate())
        .paymentType(dto.getPaymentType())
        .amountPaidCents(dto.getAmountPaidCents().longValue())
        .paCommission(dto.getPaCommissionCents().longValue())
        .remittanceInformation(dto.getRemittanceInformation())
        .transferCategory(dto.getTransferCategory())
        .debtPositionTypeOrgCode(dto.getDebtPositionTypeOrgCode())
        .balance(dto.getBalance())
        .debtor(mapPersonalDataFromPaymentNotification(dto))
        .build();
  }


  public static Person mapPersonalDataFromPaymentNotification(PaymentNotificationIngestionFlowFileDTO dto) {
    return Person.builder()
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


