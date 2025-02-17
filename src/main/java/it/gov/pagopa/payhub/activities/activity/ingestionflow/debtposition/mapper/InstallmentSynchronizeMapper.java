package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition.mapper;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentSynchronizeDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferSynchronizeDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.springframework.util.StringUtils.capitalize;

@Service
@Lazy
public class InstallmentSynchronizeMapper {

    public InstallmentSynchronizeDTO map(InstallmentIngestionFlowFileDTO installmentIngestionFlowFileDTO,
                                         Long ingestionFlowFileId,
                                         Long organizationId){
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
                .numberBeneficiary(Long.valueOf(installmentIngestionFlowFileDTO.getNumberBeneficiary()))
                .transfersList(buildTransferList(installmentIngestionFlowFileDTO))
                .build();
    }

    private static List<TransferSynchronizeDTO> buildTransferList(InstallmentIngestionFlowFileDTO dto) {
        if (Boolean.TRUE.equals(dto.getFlagMultiBeneficiary()) && dto.getNumberBeneficiary() != null && dto.getNumberBeneficiary() >= 2) {
            return IntStream.rangeClosed(2, dto.getNumberBeneficiary())
                    .mapToObj(index -> createTransfer(dto, index))
                    .filter(Objects::nonNull)
                    .toList();
        }
        return new ArrayList<>();
    }

    private static TransferSynchronizeDTO createTransfer(InstallmentIngestionFlowFileDTO dto, int index) {
        try {
            String suffix = "_" + index;

            String orgFiscalCode = (String) getFieldValue(dto, "orgFiscalCode" + suffix);
            String orgName = (String) getFieldValue(dto, "orgName" + suffix);
            BigDecimal amount = (BigDecimal) getFieldValue(dto, "amount" + suffix);
            String remittanceInformation = (String) getFieldValue(dto, "orgRemittanceInformation" + suffix);
            String iban = (String) getFieldValue(dto, "iban" + suffix);
            String category = (String) getFieldValue(dto, "category" + suffix);

            return new TransferSynchronizeDTO(
                    orgFiscalCode,
                    orgName,
                    amount,
                    remittanceInformation,
                    iban,
                    category,
                    index
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getFieldValue(InstallmentIngestionFlowFileDTO dto, String fieldName) {
        try {
            Method getter = InstallmentIngestionFlowFileDTO.class.getMethod("get" + capitalize(fieldName));
            return getter.invoke(dto);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
