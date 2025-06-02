package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
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
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;

    public IUVInstallmentsExportFlowFileDTOMapper(InstallmentOperationTypeResolver installmentOperationTypeResolver, DebtPositionTypeOrgService debtPositionTypeOrgService) {
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    }

    public IUVInstallmentsExportFlowFileDTO map(InstallmentDTO dto, Long debtPositionTypeOrgId) {
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
                .paCommissionCents(dto.getNotificationFeeCents())
                .debtPositionTypeCode(findCodeById(debtPositionTypeOrgId))
                .remittanceInformation(dto.getRemittanceInformation())
                .legacyPaymentMetadata(dto.getLegacyPaymentMetadata())
                .balance(dto.getBalance())
                .flagPuPagoPaPayment(Boolean.TRUE)
                .action(calculateInstallmentAction(dto))
                .build();
    }

    private IUVInstallmentsExportFlowFileDTO.ActionEnum calculateInstallmentAction(InstallmentDTO dto) {
        PaymentEventType paymentEventType = installmentOperationTypeResolver.calculateInstallmentOperationType(dto);

        if (PaymentEventType.DP_CREATED.equals(paymentEventType)) {
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.I;
        } else if (PaymentEventType.DP_UPDATED.equals(paymentEventType)) {
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.M;
        } else if (PaymentEventType.DP_CANCELLED.equals(paymentEventType)) {
            return IUVInstallmentsExportFlowFileDTO.ActionEnum.A;
        } else {
            throw new IllegalArgumentException("It's not possible to identify Action with paymentEventType: " + paymentEventType);
        }
    }

    private String findCodeById(Long debtPositionTypeOrgId) {
        return debtPositionTypeOrgService.getById(debtPositionTypeOrgId).getCode();
    }
}
