package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.export.debtposition.InstallmentExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.EntityIdentifierType;
import it.gov.pagopa.payhub.activities.enums.UniqueIdentifierType;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentPaidViewDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class InstallmentExportFlowFileDTOMapper {

    public InstallmentExportFlowFileDTO map(Float versionTrack, InstallmentPaidViewDTO installmentPaidViewDTO){
        switch (versionTrack.toString()){
            case "1.0" -> {
                return mapToInstallmentExportFlowFileDTO1(installmentPaidViewDTO);
            }
            case "1.1" -> {
                return mapToInstallmentExportFlowFileDTO1_1(installmentPaidViewDTO);
            }
            case "1.2" -> {
                return mapToInstallmentExportFlowFileDTO1_2(installmentPaidViewDTO);
            }
            case "1.3" -> {
                return mapToInstallmentExportFlowFileDTO1_3(installmentPaidViewDTO);
            }
            default -> throw new IllegalArgumentException("Unexpected versionTrack " + versionTrack);
        }
    }
    
    private InstallmentExportFlowFileDTO mapToInstallmentExportFlowFileDTO1(InstallmentPaidViewDTO installmentPaidViewDTO){
        Person debtor = installmentPaidViewDTO.getDebtor();

        InstallmentExportFlowFileDTO installmentExportFlowFileDTO = InstallmentExportFlowFileDTO.builder()
                .iuf(installmentPaidViewDTO.getIuf())
                .flowRowNumber(1)
                .iud(installmentPaidViewDTO.getIud())
                .iuv(installmentPaidViewDTO.getNoticeNumber())
                .domainIdentifier(installmentPaidViewDTO.getOrgFiscalCode())
                .receiptMessageIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .receiptMessageDateTime(installmentPaidViewDTO.getPaymentDateTime())
                .requestMessageReference(installmentPaidViewDTO.getPaymentReceiptId())
                .requestDateTimeReference(installmentPaidViewDTO.getPaymentDateTime())
                .uniqueIdentifierType(UniqueIdentifierType.B)
                .uniqueIdentifierCode(installmentPaidViewDTO.getIdPsp())
                .attestingName(installmentPaidViewDTO.getPspCompanyName())
                .beneficiaryEntityType(EntityIdentifierType.G)
                .beneficiaryUniqueIdentifierCode(installmentPaidViewDTO.getOrgFiscalCode())
                .beneficiaryName(installmentPaidViewDTO.getCompanyName())
                .debtorEntityType(debtor.getEntityType() != null ? EntityIdentifierType.fromValue(debtor.getEntityType().getValue()) : null)
                .debtorIndentifierCode(debtor.getFiscalCode() != null ? debtor.getFiscalCode() : "ANONIMO")
                .debtorFullName(debtor.getFullName())
                .debtorAddress(debtor.getAddress())
                .debtorStreetNumber(debtor.getCivic())
                .debtorPostalCode(debtor.getPostalCode())
                .debtorCity(debtor.getLocation())
                .debtorProvince(debtor.getProvince())
                .debtorCountry(debtor.getNation())
                .debtorEmail(debtor.getEmail())
                .paymentOutcomeCode(0)
                .totalAmountPaid(Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getPaymentAmountCents()))
                .uniquePaymentIdentifier(installmentPaidViewDTO.getCreditorReferenceId())
                .paymentContextCode(installmentPaidViewDTO.getPaymentReceiptId())
                .singleAmountPaid(Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getAmountCents()))
                .singlePaymentOutcome("0")
                .singlePaymentOutcomeDateTime(installmentPaidViewDTO.getPaymentDateTime())
                .uniqueCollectionIdentifier(installmentPaidViewDTO.getPaymentReceiptId())
                .build();

            if (installmentPaidViewDTO.getPayer() != null){
                Person payer = installmentPaidViewDTO.getPayer();
                installmentExportFlowFileDTO.setPayerEntityType(payer.getEntityType() != null ? EntityIdentifierType.fromValue(payer.getEntityType().getValue()): null);
                installmentExportFlowFileDTO.setPayerUniqueIdentifierCode(payer.getFiscalCode() != null ? payer.getFiscalCode() : "ANONIMO");
                installmentExportFlowFileDTO.setPayerFullName(payer.getFullName());
                installmentExportFlowFileDTO.setPayerAddress(payer.getAddress());
                installmentExportFlowFileDTO.setPayerStreetNumber(payer.getCivic());
                installmentExportFlowFileDTO.setPayerPostalCode(payer.getPostalCode());
                installmentExportFlowFileDTO.setPayerCity(payer.getLocation());
                installmentExportFlowFileDTO.setPayerProvince(payer.getProvince());
                installmentExportFlowFileDTO.setPayerCountry(payer.getNation());
                installmentExportFlowFileDTO.setPayerEmail(payer.getEmail());
            }

            return installmentExportFlowFileDTO;
    }

    private InstallmentExportFlowFileDTO mapToInstallmentExportFlowFileDTO1_1(InstallmentPaidViewDTO installmentPaidViewDTO){
        InstallmentExportFlowFileDTO installmentExportFlowFileDTO = mapToInstallmentExportFlowFileDTO1(installmentPaidViewDTO);

        installmentExportFlowFileDTO.setPaymentReason(installmentPaidViewDTO.getRemittanceInformation());
        installmentExportFlowFileDTO.setCollectionSpecificData("9/".concat(installmentPaidViewDTO.getCategory()));
        installmentExportFlowFileDTO.setDueType(installmentPaidViewDTO.getCode());
        installmentExportFlowFileDTO.setRt(null); //TODO field rt depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
        installmentExportFlowFileDTO.setSinglePaymentDataIndex(installmentPaidViewDTO.getTransferIndex());
        installmentExportFlowFileDTO.setPspAppliedFees(installmentPaidViewDTO.getFeeCents() != null ? Utilities.longCentsToBigDecimalEuro(installmentPaidViewDTO.getFeeCents()) : null);

        if (installmentPaidViewDTO.getCode().equals("MARCA_BOLLO")){
            installmentExportFlowFileDTO.setReceiptAttachmentType("BD");
            installmentExportFlowFileDTO.setReceiptAttachmentTest(null); //TODO field blbRtDatiPagDatiSingPagAllegatoRicevutaTest depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
        }

        return installmentExportFlowFileDTO;
    }


    private InstallmentExportFlowFileDTO mapToInstallmentExportFlowFileDTO1_2(InstallmentPaidViewDTO installmentPaidViewDTO){
        InstallmentExportFlowFileDTO installmentExportFlowFileDTO = mapToInstallmentExportFlowFileDTO1_1(installmentPaidViewDTO);

        installmentExportFlowFileDTO.setBalance(installmentPaidViewDTO.getBalance());

        return installmentExportFlowFileDTO;
    }

    private InstallmentExportFlowFileDTO mapToInstallmentExportFlowFileDTO1_3(InstallmentPaidViewDTO installmentPaidViewDTO){
        InstallmentExportFlowFileDTO installmentExportFlowFileDTO = mapToInstallmentExportFlowFileDTO1_2(installmentPaidViewDTO);

        installmentExportFlowFileDTO.setOrgFiscalCode(installmentPaidViewDTO.getOrgFiscalCode());
        installmentExportFlowFileDTO.setOrgName(installmentPaidViewDTO.getCompanyName());
        installmentExportFlowFileDTO.setDueTaxonomicCode(installmentPaidViewDTO.getCategory());

        return installmentExportFlowFileDTO;
    }
}
