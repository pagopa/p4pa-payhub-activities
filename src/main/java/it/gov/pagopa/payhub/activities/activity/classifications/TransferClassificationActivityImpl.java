package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Slf4j
@Component
public class TransferClassificationActivityImpl implements TransferClassificationActivity {
	private final ClassificationService classificationService;
	private final TransferService transferService;
	private final PaymentsReportingService paymentsReportingService;
	private final TransferClassificationService transferClassificationService;
	private final TransferClassificationStoreService transferClassificationStoreService;
	private final TreasuryService treasuryService;

	public TransferClassificationActivityImpl(ClassificationService classificationService,
	                                          TransferService transferService,
                                              PaymentsReportingService paymentsReportingService,
                                              TransferClassificationService transferClassificationService,
                                              TransferClassificationStoreService transferClassificationStoreService,
											  TreasuryService treasuryService) {
		this.classificationService = classificationService;
		this.transferService = transferService;
		this.paymentsReportingService = paymentsReportingService;
		this.transferClassificationService = transferClassificationService;
		this.transferClassificationStoreService = transferClassificationStoreService;
        this.treasuryService = treasuryService;
    }

	@Override
	public void classify(TransferSemanticKeyDTO transferSemanticKey) {
		log.info("Transfer classification for organization id: {} and iuv: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv());
		Long deletedRowsNumber = classificationService.deleteBySemanticKey(transferSemanticKey);
		log.debug("Deleted {} classifications for organization id: {} and iuv: {}",
			deletedRowsNumber, transferSemanticKey.getOrgId(), transferSemanticKey.getIuv());
		Transfer transferDTO = transferService.findBySemanticKey(transferSemanticKey).orElse(null);

		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex());
		PaymentsReporting paymentsReporting = paymentsReportingService.getBySemanticKey(transferSemanticKey);
		Treasury treasuryDTO = retrieveTreasury(transferSemanticKey.getOrgId(), paymentsReporting);

		List<ClassificationsEnum> classifications = transferClassificationService.defineLabels(transferDTO, paymentsReporting, treasuryDTO);
		log.info("Labels defined for organization id: {} and iuv: {} and iur {} and transfer index: {} are: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex(),
			String.join(", ", classifications.stream().map(String::valueOf).toList()));

		transferClassificationStoreService.saveClassifications(transferSemanticKey, transferDTO, paymentsReporting, treasuryDTO, classifications);
		notifyReportedTransferId(transferDTO, paymentsReporting);
	}

	/**
	 * Retrieves the {@link Treasury} record for the given ID.
	 *
	 * @param orgId the ID of the organization
	 * @param paymentsReportingDTO the payments reporting data transfer object containing payment reporting details
	 * @return the {@link Treasury} corresponding to the given ID
	 */
	private Treasury retrieveTreasury(Long orgId, PaymentsReporting paymentsReportingDTO) {
		if (paymentsReportingDTO != null) {
			String iuf = paymentsReportingDTO.getIuf();
			log.info("Retrieve treasury for organization id: {} and iuf {}", orgId, iuf);
			return treasuryService.getByOrganizationIdAndIuf(orgId, iuf)
					.orElseThrow(() -> new InvalidValueException("invalid Treasury"));
		}
		return null;
	}

	/**
	 * Notify the status of the given transfer as Reported.
	 *
	 * @param transferDTO the transfer data transfer object containing transfer details
	 * @param paymentsReportingDTO the payments reporting data transfer object containing payment reporting details
	 */
	private void notifyReportedTransferId(Transfer transferDTO, PaymentsReporting paymentsReportingDTO) {
		if(transferDTO != null && paymentsReportingDTO != null) {
			transferService.notifyReportedTransferId(transferDTO.getTransferId());
		}
	}
}
