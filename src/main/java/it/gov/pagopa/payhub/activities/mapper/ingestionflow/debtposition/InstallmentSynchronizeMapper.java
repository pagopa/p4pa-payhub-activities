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

    private static final List<String> COLUMN_NAMES_ORG_FISCALCODE = List.of("codiceFiscaleEnte", "orgFiscalCode");
    private static final List<String> COLUMN_NAMES_ORG_NAME = List.of("denominazioneEnte", "orgName");
    private static final List<String> COLUMN_NAMES_AMOUNT_CENTS = List.of("importoVersamentoEnte", "amount");
    private static final List<String> COLUMN_NAMES_REMITTANCE_INFORMATION = List.of("causaleVersamentoEnte", "remittanceInformation");
    private static final List<String> COLUMN_NAMES_IBAN = List.of("ibanAccreditoEnte", "iban");
    private static final List<String> COLUMN_NAMES_CATEGORY = List.of("codiceTassonomiaEnte", "category", "datiSpecificiRiscossioneEnte");

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
                .generateNotice(installmentIngestionFlowFileDTO.getGenerateNotice())
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

        List<String> columnSuffixes;
        if(index != 2){
            columnSuffixes = List.of("_" + index);
        } else {
            columnSuffixes = List.of("_" + index, "Secondario");
        }

        return TransferSynchronizeDTO.builder()
                .orgFiscalCode(getFirstValue(transferMap, COLUMN_NAMES_ORG_FISCALCODE, columnSuffixes))
                .orgName(getFirstValue(transferMap, COLUMN_NAMES_ORG_NAME, columnSuffixes))
                .amountCents(bigDecimalEuroToLongCentsAmount(new BigDecimal(getFirstValue(transferMap, COLUMN_NAMES_AMOUNT_CENTS, columnSuffixes))))
                .remittanceInformation(getFirstValue(transferMap, COLUMN_NAMES_REMITTANCE_INFORMATION, columnSuffixes))
                .iban(getFirstValue(transferMap, COLUMN_NAMES_IBAN, columnSuffixes))
                .category(getFirstValue(transferMap, COLUMN_NAMES_CATEGORY, columnSuffixes))
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

    private String getFirstValue(MultiValuedMap<String, String> map, List<String> columnNames, List<String> suffixes) {
        return columnNames.stream()
                .flatMap(c ->
                    suffixes.stream()
                            .map(s -> c + s)
                )
                .flatMap(k -> map.get(k).stream())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing required value for keys: %s".formatted(columnNames)));
    }

}
