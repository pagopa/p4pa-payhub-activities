package it.gov.pagopa.payhub.activities.activity.pagopapayments;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class FolderStatusActivityImpl implements FolderStatusActivity {
    private final PrintPaymentNoticeService printPaymentNoticeService;

    public FolderStatusActivityImpl(PrintPaymentNoticeService printPaymentNoticeService) {
        this.printPaymentNoticeService = printPaymentNoticeService;
    }

    @Override
    public SignedUrlResultDTO retrieveFolderStatus(Long organizationId, String folderId) {
        return printPaymentNoticeService.getSignedUrl(organizationId, folderId);
    }
}
