package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity {
    private final PaymentsReportingService paymentsReportingService;
    private final TransferClassificationStoreService transferClassificationStoreService;
    private final TreasuryService treasuryService;

    public IufClassificationActivityImpl(PaymentsReportingService paymentsReportingService, TransferClassificationStoreService transferClassificationStoreService, TreasuryService treasuryService) {
        this.paymentsReportingService = paymentsReportingService;
        this.transferClassificationStoreService = transferClassificationStoreService;
	    this.treasuryService = treasuryService;
    }

    @Override
    public IufClassificationActivityResult classifyIuf(Long organizationId, String treasuryId, String iuf) {
        log.info("Starting IUF Classification for organization id {} and iuf {}", organizationId,iuf);

        Treasury treasury = treasuryService.getById(treasuryId);

        if (treasury.getBillAmountCents() < 0) {
            log.info("Skipping IUF Classification for organization id {} and iuf {} due to negative bill amount cents in treasury with treasury id {}", organizationId, iuf, treasuryId);
            return IufClassificationActivityResult.builder()
                    .organizationId(organizationId)
                    .transfers2classify(Collections.emptyList())
                    .build();
        }

        List<Transfer2ClassifyDTO> transfers2classify =
            Objects.requireNonNull(paymentsReportingService.getByOrganizationIdAndIuf(organizationId, iuf).getEmbedded()).getPaymentsReportings()
            .stream()
            .map(paymentsReportingDTO ->
                Transfer2ClassifyDTO.builder()
                    .iuv(paymentsReportingDTO.getIuv())
                    .iur(paymentsReportingDTO.getIur())
                    .transferIndex(paymentsReportingDTO.getTransferIndex())
                    .build())
            .toList();

        if (transfers2classify.isEmpty()) {
            log.debug("Saving payments reporting found for organization id {} and iuf: {}", organizationId, iuf);
            saveClassification(organizationId, treasuryId, iuf);
        }

        return IufClassificationActivityResult.builder()
                .organizationId(organizationId)
                .transfers2classify(transfers2classify)
                .build();
    }

    /**
     * save classification data
     *
     * @param organizationId organization id
     * @param treasuryId  treasury id
     * @param iuf  flow unique identifier
     */
    private void saveClassification(Long organizationId, String treasuryId, String iuf) {
        log.debug("retrieving treasury from ID {}", treasuryId);
        Treasury treasury = treasuryService.getById(treasuryId);

        log.debug("Saving classification TES_NO_IUF_OR_IUV for organizationId: {} - treasuryId: {} - iuf: {}", organizationId, treasuryId, iuf);
        transferClassificationStoreService.saveIufClassifications(treasury, List.of(ClassificationsEnum.TES_NO_IUF_OR_IUV));
    }
}