package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static it.gov.pagopa.payhub.activities.util.Utilities.longCentsToBigDecimalEuro;

@Lazy
@Service
public class IUVInstallmentsExportFlowFileDTOMapper {

    public IUVInstallmentsExportFlowFileDTO map(InstallmentDTO dto) {
        PersonDTO debtor = dto.getDebtor();

        return IUVInstallmentsExportFlowFileDTO.builder()
                .iud(dto.getIud())
                .iuv(dto.getIuv())
                .entityType(IUVInstallmentsExportFlowFileDTO.EntityTypeEnum.valueOf(debtor.getEntityType().name()))
                .fiscalCode(debtor.getFiscalCode())
                .fullName(debtor.getFullName())
                .address(debtor.getAddress())
                .civic(debtor.getCivic())
                .postalCode(debtor.getPostalCode())
                .location(debtor.getLocation())
                .province(debtor.getProvince())
                .nation(debtor.getNation())
                .email(debtor.getEmail())
                .dueDate(dto.getDueDate())
                .amount(longCentsToBigDecimalEuro(dto.getAmountCents()))
                .paCommissionCents(longCentsToBigDecimalEuro(dto.getNotificationFeeCents()))
                .debtPositionTypeCode(null)
                .paymentType("MPA")
                .remittanceInformation(dto.getRemittanceInformation())
                .legacyPaymentMetadata(dto.getLegacyPaymentMetadata())
                .balance(dto.getBalance())
                .flagPuPagoPaPayment(Boolean.TRUE)
                .action(IUVInstallmentsExportFlowFileDTO.ActionEnum.valueOf("I")) // TODO da rivedere
                .build();
    }

}
