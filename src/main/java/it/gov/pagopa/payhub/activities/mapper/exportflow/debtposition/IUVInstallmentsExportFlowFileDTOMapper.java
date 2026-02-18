package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.IUVInstallmentsExportFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.payhub.activities.util.Utilities.longCentsToBigDecimalEuro;

@Lazy
@Service
public class IUVInstallmentsExportFlowFileDTOMapper {

    public IUVInstallmentsExportFlowFileDTO map(InstallmentDTO dto, DebtPositionTypeOrg debtPositionTypeOrg) {
        PersonDTO debtor = dto.getDebtor();
        List<TransferDTO> transferDTOList = dto.getTransfers();

         IUVInstallmentsExportFlowFileDTO iuvInstallmentsExportFlowFileDTO = IUVInstallmentsExportFlowFileDTO.builder()
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
                .debtPositionTypeCode(debtPositionTypeOrg.getCode())
                .paymentType("ALL")
                .remittanceInformation(Optional.ofNullable(
                                dto.getOriginalRemittanceInformation())
                        .orElse(dto.getRemittanceInformation()))
                .legacyPaymentMetadata(dto.getLegacyPaymentMetadata())
                .balance(dto.getBalance())
                .generateNotice(dto.getGenerateNotice())
                .flagMultiBeneficiary(transferDTOList.size() == 1 ? Boolean.FALSE : Boolean.TRUE)
                .action(dto.getIngestionFlowFileAction())
                .build();

         TransferDTO transfer2 = transferDTOList.stream()
                 .filter(transferDTO -> transferDTO.getTransferIndex() == 2)
                 .findFirst().orElse(null);

         if(transfer2 != null) {
            iuvInstallmentsExportFlowFileDTO.setOrgFiscalCode2(transfer2.getOrgFiscalCode());
            iuvInstallmentsExportFlowFileDTO.setOrgName2(transfer2.getOrgName());
            iuvInstallmentsExportFlowFileDTO.setOrgIban2(transfer2.getIban());
            iuvInstallmentsExportFlowFileDTO.setOrgLegacyPaymentMetadata2(transfer2.getCategory());
            iuvInstallmentsExportFlowFileDTO.setOrgRemittanceInformation2(transfer2.getRemittanceInformation());
            iuvInstallmentsExportFlowFileDTO.setOrgAmount2(longCentsToBigDecimalEuro(transfer2.getAmountCents()));
         }

         return iuvInstallmentsExportFlowFileDTO;
    }
}
