package it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static it.gov.pagopa.payhub.activities.util.Utilities.bigDecimalEuroToLongCentsAmount;
import static it.gov.pagopa.payhub.activities.util.Utilities.toOffsetDateTimeEndOfTheDay;

@Service
@Lazy
public class InstallmentSynchronizeMapper {

    private final ObjectMapper objectMapper;

    public InstallmentSynchronizeMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InstallmentSynchronizeDTO map(InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO,
                                         Long ingestionFlowFileId,
                                         Long ingestionFlowFileLineNumber,
                                         Long organizationId,
                                         String fileName) {
        return InstallmentSynchronizeDTO.builder()
                .ingestionFlowFileId(ingestionFlowFileId)
                .ingestionFlowFileLineNumber(ingestionFlowFileLineNumber)
                .organizationId(organizationId)
                .action(installmentIngestionFlowFileDTO.getAction())
                .draft(installmentIngestionFlowFileDTO.getDraft())
                .iupdOrg(installmentIngestionFlowFileDTO.getIupdOrg())
                .description(installmentIngestionFlowFileDTO.getDescription())
                .validityDate(installmentIngestionFlowFileDTO.getValidityDate())
                .multiDebtor(installmentIngestionFlowFileDTO.getMultiDebtor())
                .notificationDate(toOffsetDateTimeEndOfTheDay(installmentIngestionFlowFileDTO.getNotificationDate()))
                .paymentOptionIndex(installmentIngestionFlowFileDTO.getPaymentOptionIndex())
                .paymentOptionType(installmentIngestionFlowFileDTO.getPaymentOptionType())
                .paymentOptionDescription(installmentIngestionFlowFileDTO.getPaymentOptionDescription())
                .iud(installmentIngestionFlowFileDTO.getIud())
                .iuv(installmentIngestionFlowFileDTO.getIuv())
                .entityType(installmentIngestionFlowFileDTO.getEntityType())
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
                .amountCents(bigDecimalEuroToLongCentsAmount(installmentIngestionFlowFileDTO.getAmount()))
                .debtPositionTypeCode(installmentIngestionFlowFileDTO.getDebtPositionTypeCode())
                .remittanceInformation(installmentIngestionFlowFileDTO.getRemittanceInformation())
                .legacyPaymentMetadata(installmentIngestionFlowFileDTO.getLegacyPaymentMetadata())
                .flagPuPagoPaPayment(installmentIngestionFlowFileDTO.getFlagPuPagoPaPayment())
                .balance(installmentIngestionFlowFileDTO.getBalance())
                .flagMultibeneficiary(installmentIngestionFlowFileDTO.getFlagMultiBeneficiary())
                .numberBeneficiary(installmentIngestionFlowFileDTO.getNumberBeneficiary() != null ? installmentIngestionFlowFileDTO.getNumberBeneficiary() : null)
                .additionalTransfers(buildAdditionalTransferList(installmentIngestionFlowFileDTO))
                .executionConfig(mapExecutionConfig(installmentIngestionFlowFileDTO.getExecutionConfig()))
                .ingestionFlowFileName(fileName)
                .build();
    }

    private JsonNode mapExecutionConfig(String executionConfig) {
        if (StringUtils.isBlank(executionConfig)) {
            return NullNode.instance;
        }

        try {
            return objectMapper.readTree(executionConfig);
        } catch (JsonProcessingException e) {
            throw new InvalidValueException(String.format("Invalid execution config value: [%s] ", executionConfig));
        }
    }

    private List<TransferSynchronizeDTO> buildAdditionalTransferList(InstallmentIngestionFlowFileDTO dto) {
        int nBeneficiary = Optional.ofNullable(dto.getNumberBeneficiary()).orElse(1);
        if (Boolean.TRUE.equals(dto.getFlagMultiBeneficiary()) && nBeneficiary >= 2) {
            return IntStream.rangeClosed(2, nBeneficiary)
                    .mapToObj(index -> createTransfer(dto, index))
                    .toList();
        }
        return List.of();
    }


    private TransferSynchronizeDTO createTransfer(InstallmentIngestionFlowFileDTO dto, int index) {
        MultiValuedMap<String, String> transferMap = getTransferMapByIndex(dto, index);

        if (transferMap == null) {
            throw new IllegalStateException("Missing or empty transfer map for index: " + index);
        }

        return TransferSynchronizeDTO.builder()
                .orgFiscalCode(getFirstValue(transferMap, "codiceFiscaleEnte", "orgFiscalCode", index))
                .orgName(getFirstValue(transferMap, "denominazioneEnte", "orgName", index))
                .amountCents(bigDecimalEuroToLongCentsAmount(new BigDecimal(getFirstValue(transferMap, "importoVersamentoEnte", "amount", index))))
                .remittanceInformation(getFirstValue(transferMap, "causaleVersamentoEnte", "remittanceInformation", index))
                .iban(getFirstValue(transferMap, "ibanAccreditoEnte", "iban", index))
                .category(getFirstValue(transferMap, "codiceTassonomiaEnte", "category", index))
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

    private String getFirstValue(MultiValuedMap<String, String> map, String italianKey, String englishKey, int index) {
        return Optional.ofNullable(map)
                .map(m -> m.get(italianKey + "_" + index))
                .flatMap(values -> values.stream().findFirst())
                .or(() -> Optional.ofNullable(map)
                        .map(m -> m.get(englishKey + "_" + index))
                        .flatMap(values -> values.stream().findFirst()))
                .orElseThrow(() -> new IllegalArgumentException("Missing required value for keys: %s or %s".formatted(italianKey, englishKey)));
    }

}
