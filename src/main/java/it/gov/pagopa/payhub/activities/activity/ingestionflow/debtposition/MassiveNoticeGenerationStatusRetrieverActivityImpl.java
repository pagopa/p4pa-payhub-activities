package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class MassiveNoticeGenerationStatusRetrieverActivityImpl implements MassiveNoticeGenerationStatusRetrieverActivity {
    private final PrintPaymentNoticeService printPaymentNoticeService;

    public MassiveNoticeGenerationStatusRetrieverActivityImpl(PrintPaymentNoticeService printPaymentNoticeService) {
        this.printPaymentNoticeService = printPaymentNoticeService;
    }

    @Override
    public SignedUrlResultDTO retrieveNoticesGenerationStatus(Long organizationId, String pdfGeneratedId) {
        return printPaymentNoticeService.getSignedUrl(organizationId, pdfGeneratedId);
    }
}
