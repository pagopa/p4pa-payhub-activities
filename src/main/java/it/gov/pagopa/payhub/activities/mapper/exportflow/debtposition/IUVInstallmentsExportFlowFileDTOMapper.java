package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static it.gov.pagopa.payhub.activities.util.Utilities.longCentsToBigDecimalEuro;

@Lazy
@Service
public class IUVInstallmentsExportFlowFileDTOMapper {

    private final DebtPositionTypeOrgService debtPositionTypeOrgService;

    public IUVInstallmentsExportFlowFileDTOMapper(DebtPositionTypeOrgService debtPositionTypeOrgService) {
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
                .action(dto.getIngestionFlowFileAction())
                .build();
    }

    private String findCodeById(Long debtPositionTypeOrgId) {
        return debtPositionTypeOrgService.getById(debtPositionTypeOrgId).getCode();
    }
}
