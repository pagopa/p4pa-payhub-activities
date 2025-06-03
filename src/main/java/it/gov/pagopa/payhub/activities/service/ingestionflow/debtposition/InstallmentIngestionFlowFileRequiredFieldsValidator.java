package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstallmentIngestionFlowFileRequiredFieldsValidator {

    private InstallmentIngestionFlowFileRequiredFieldsValidator() {
    }

    public static final String CREATION_DATE_FORMAT = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public static void validateRequiredFields(InstallmentIngestionFlowFileDTO dto) {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("EntityType", dto.getEntityType());
        requiredFields.put("FiscalCode", dto.getFiscalCode());
        requiredFields.put("FullName", dto.getFullName());
        requiredFields.put("Amount", dto.getAmount());
        requiredFields.put("DebtPositionTypeCode", dto.getDebtPositionTypeCode());
        requiredFields.put("RemittanceInformation", dto.getRemittanceInformation());
        requiredFields.put("Action", dto.getAction());


        List<String> missingFields = requiredFields.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        if (!missingFields.isEmpty()) {
            throw new InvalidIngestionFileException("Missing required fields: " + String.join(", ", missingFields));
        }

        setDefaultValues(dto);
        setDefaultIfNotLastVersion(dto);
    }

    private static void setDefaultValues(InstallmentIngestionFlowFileDTO dto){
        if (dto.getFlagPagoPaPayment() == null) {
            dto.setFlagPagoPaPayment(Boolean.TRUE);
        }

        if (dto.getFlagMultiBeneficiary() == null) {
            dto.setFlagMultiBeneficiary(Boolean.FALSE);
        }

        if (dto.getNumberBeneficiary() == null) {
            dto.setNumberBeneficiary(Boolean.TRUE.equals(dto.getFlagMultiBeneficiary()) ? 1 : 0);
        }
    }

    private static void setDefaultIfNotLastVersion(InstallmentIngestionFlowFileDTO dto) {
        if (dto.getDescription() == null) {
            dto.setDescription(String.format("DebtPosition with code %s was created on %s", dto.getDebtPositionTypeCode(), CREATION_DATE_FORMAT));
        }
        if (dto.getPaymentOptionIndex() == null) {
            dto.setPaymentOptionIndex(1);
        }
        if (dto.getPaymentOptionType() == null) {
            dto.setPaymentOptionType("SINGLE_INSTALLMENT");
        }
        if (dto.getPaymentOptionDescription() == null) {
            dto.setPaymentOptionDescription("Pagamento Singolo Avviso");
        }
    }
}
