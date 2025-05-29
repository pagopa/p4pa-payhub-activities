package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.debtposition.InstallmentOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static it.gov.pagopa.payhub.activities.util.Utilities.longCentsToBigDecimalEuro;

@Lazy
@Service
public class IUVInstallmentsExportFlowFileDTOMapper {

    private final InstallmentOperationTypeResolver installmentOperationTypeResolver;

    public IUVInstallmentsExportFlowFileDTOMapper(InstallmentOperationTypeResolver installmentOperationTypeResolver) {
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
    }

    public IUVInstallmentsExportFlowFileDTO map(InstallmentDTO dto) {
        PersonDTO debtor = dto.getDebtor();

        return IUVInstallmentsExportFlowFileDTO.builder()
                .iud(dto.getIud())
                .iuv(dto.getIuv())
                .entityType(debtor.getEntityType())
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
                .action(calculateInstallmentAction(dto))
                .build();
    }

    private IUVInstallmentsExportFlowFileDTO.ActionEnum calculateInstallmentAction(InstallmentDTO dto){
        PaymentEventType paymentEventType = installmentOperationTypeResolver.calculateInstallmentOperationType(dto);

        if (paymentEventType.equals(PaymentEventType.DP_CREATED)){
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.I;
        } else if (paymentEventType.equals(PaymentEventType.DP_UPDATED)){
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.M;
        } else if (paymentEventType.equals(PaymentEventType.DP_CANCELLED)){
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.A;
        } else {
            throw new IllegalArgumentException("It's not possible to identify Action with paymentEventType: " + paymentEventType);
        }
    }
}
