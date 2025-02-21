package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Lazy
public class InstallmentSynchronizeMapper {

    public InstallmentSynchronizeDTO map(InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO,
                                         Long ingestionFlowFileId,
                                         Long organizationId) {
        return InstallmentSynchronizeDTO.builder()
                .ingestionFlowFileId(ingestionFlowFileId)
                .ingestionFlowFileLineNumber(installmentIngestionFlowFileDTO.getIngestionFlowFileLineNumber())
                .organizationId(organizationId)
                .action(InstallmentSynchronizeDTO.ActionEnum.valueOf(installmentIngestionFlowFileDTO.getAction().name()))
                .draft(installmentIngestionFlowFileDTO.getDraft())
                .iupdOrg(installmentIngestionFlowFileDTO.getIupdOrg())
                .description(installmentIngestionFlowFileDTO.getDescription())
                .validityDate(installmentIngestionFlowFileDTO.getValidityDate())
                .multiDebtor(installmentIngestionFlowFileDTO.getMultiDebtor())
                .notificationDate(installmentIngestionFlowFileDTO.getNotificationDate())
                .paymentOptionIndex(Long.valueOf(installmentIngestionFlowFileDTO.getPaymentOptionIndex()))
                .paymentOptionType(installmentIngestionFlowFileDTO.getPaymentOptionType())
                .iud(installmentIngestionFlowFileDTO.getIud())
                .iuv(installmentIngestionFlowFileDTO.getIuv())
                .entityType(InstallmentSynchronizeDTO.EntityTypeEnum.valueOf(installmentIngestionFlowFileDTO.getEntityType().name()))
                .fiscalCode(installmentIngestionFlowFileDTO.getFiscalCode())
                .fullName(installmentIngestionFlowFileDTO.getFullName())
                .address(installmentIngestionFlowFileDTO.getAddress())
                .civic(installmentIngestionFlowFileDTO.getCivic())
                .postalCode(installmentIngestionFlowFileDTO.getPostalCode())
                .location(installmentIngestionFlowFileDTO.getLocation())
                .province(installmentIngestionFlowFileDTO.getProvince())
                .nation(installmentIngestionFlowFileDTO.getNation())
                .email(installmentIngestionFlowFileDTO.getEmail())
                .dueDate(installmentIngestionFlowFileDTO.getDueDate())
                .amount(installmentIngestionFlowFileDTO.getAmount())
                .debtPositionTypeCode(installmentIngestionFlowFileDTO.getDebtPositionTypeCode())
                .paymentTypeCode(installmentIngestionFlowFileDTO.getPaymentTypeCode())
                .remittanceInformation(installmentIngestionFlowFileDTO.getRemittanceInformation())
                .legacyPaymentMetadata(installmentIngestionFlowFileDTO.getLegacyPaymentMetadata())
                .flagPagoPaPayment(installmentIngestionFlowFileDTO.getFlagPagoPaPayment())
                .balance(installmentIngestionFlowFileDTO.getBalance())
                .flagMultibeneficiary(installmentIngestionFlowFileDTO.getFlagMultiBeneficiary())
                .numberBeneficiary(installmentIngestionFlowFileDTO.getNumberBeneficiary() != null ? Long.valueOf(installmentIngestionFlowFileDTO.getNumberBeneficiary()) : null)
                .transfersList(buildTransferList(installmentIngestionFlowFileDTO))
                .build();
    }

    private List<TransferSynchronizeDTO> buildTransferList(InstallmentIngestionFlowFileDTO dto) {
        if (Boolean.TRUE.equals(dto.getFlagMultiBeneficiary()) && Optional.ofNullable(dto.getNumberBeneficiary()).orElse(1) >= 2) {
            return IntStream.rangeClosed(2, dto.getNumberBeneficiary())
                    .mapToObj(index -> createTransfer(dto, index))
                    .toList();
        }
        return List.of();
    }

    private TransferSynchronizeDTO createTransfer(InstallmentIngestionFlowFileDTO dto, int index) {
        MultiValuedMap<String, String> transferMap = getTransferMapByIndex(dto, index);
        if (transferMap == null) {
            return null;
        }

        return TransferSynchronizeDTO.builder()
                .orgFiscalCode(getFirstValue(transferMap, "orgFiscalCode"))
                .orgName(getFirstValue(transferMap, "orgName"))
                .amount(getNullableBigDecimal(transferMap))
                .remittanceInformation(getFirstValue(transferMap, "orgRemittanceInformation"))
                .iban(getFirstValue(transferMap, "iban"))
                .category(getFirstValue(transferMap, "category"))
                .transferIndex(index)
                .build();
    }

    private MultiValuedMap<String, String> getTransferMapByIndex(InstallmentIngestionFlowFileDTO dto, int index) {
        return switch (index) {
            case 2 -> dto.getTransfer2();
            case 3 -> dto.getTransfer3();
            case 4 -> dto.getTransfer4();
            case 5 -> dto.getTransfer5();
            default -> null;
        };
    }

    private String getFirstValue(MultiValuedMap<String, String> map, String key) {
        return Optional.ofNullable(map.get(key))
                .flatMap(values -> values.stream().findFirst())
                .orElse(null);
    }

    private BigDecimal getNullableBigDecimal(MultiValuedMap<String, String> map) {
        return Optional.ofNullable(getFirstValue(map, "amount"))
                .filter(value -> !value.isBlank())
                .map(BigDecimal::new)
                .orElse(null);
    }
}
