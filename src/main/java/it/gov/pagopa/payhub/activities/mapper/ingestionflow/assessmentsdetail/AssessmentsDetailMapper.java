package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AssessmentsDetailMapper {
    private final String dataCipherPsw;


    public AssessmentsDetailMapper(
            @Value("${cipher.file-encrypt-psw}") String dataCipherPsw) {
        this.dataCipherPsw = dataCipherPsw;
    }

    public AssessmentsDetailRequestBody map2AssessmentsDetailRequestBody(AssessmentsIngestionFlowFileDTO dto, Long organizationId, Long assessmentId, ReceiptDTO receiptDTO) {

        byte [] cfDebtorHash = AESUtils.encrypt(this.dataCipherPsw, receiptDTO.getDebtor().getFiscalCode());

        return AssessmentsDetailRequestBody.builder()
                .assessmentId(assessmentId)
                .organizationId(organizationId)
                .debtPositionTypeOrgCode(dto.getDebtPositionTypeOrgCode())
                .iuv(dto.getIuv())
                .iud(dto.getIud())
                .iur(receiptDTO.getPaymentReceiptId())
                .debtorFiscalCodeHash(cfDebtorHash)
                .officeCode(dto.getOfficeCode())
                .sectionCode(dto.getSectionCode())
                .assessmentCode(dto.getAssessmentCode())
                .amountCents(dto.getAmountCents())
                .amountSubmitted(dto.getAmountSubmitted())
                .receiptId(receiptDTO.getReceiptId())
                .paymentDateTime(receiptDTO.getPaymentDateTime())
                .build();
    }
}
