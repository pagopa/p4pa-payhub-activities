package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
        MultiValuedMap<String, String> map = dto.getTransfers();
        if (Boolean.TRUE.equals(dto.getFlagMultiBeneficiary()) && dto.getNumberBeneficiary() != null && dto.getNumberBeneficiary() >= 2) {
            return IntStream.rangeClosed(2, dto.getNumberBeneficiary())
                    .mapToObj(index -> createTransfer(map, index))
                    .toList();
        }
        return List.of();
    }

    private TransferSynchronizeDTO createTransfer(MultiValuedMap<String, String> map, int index) {
        return TransferSynchronizeDTO.builder()
                .orgFiscalCode(getFirstValue(map, "orgFiscalCode_" + index))
                .orgName(getFirstValue(map, "orgName_" + index))
                .amount(getNullableBigDecimal(map, "amount_" + index))
                .remittanceInformation(getFirstValue(map, "orgRemittanceInformation_" + index))
                .iban(getFirstValue(map, "iban_" + index))
                .category(getFirstValue(map, "category_" + index))
                .transferIndex(index)
                .build();
    }

    private String getFirstValue(MultiValuedMap<String, String> map, String key) {
        return map.containsKey(key) && map.get(key) != null
                ? map.get(key).stream().findFirst().orElse(null)
                : null;
    }

    private BigDecimal getNullableBigDecimal(MultiValuedMap<String, String> map, String key) {
        String value = getFirstValue(map, key);
        return (value != null && !value.isBlank()) ? new BigDecimal(value) : null;
    }

}
