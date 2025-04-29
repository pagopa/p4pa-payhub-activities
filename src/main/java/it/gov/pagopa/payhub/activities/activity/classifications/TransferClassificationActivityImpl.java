package it.gov.pagopa.payhub.activities.activity.classifications;

import static it.gov.pagopa.payhub.activities.util.DebtPositionUtilities.INSTALLMENT_PAYED_STATUSES_SET;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
	private final InstallmentService installmentService;
	private final PaymentNotificationService paymentNotificationService;

	public TransferClassificationActivityImpl(ClassificationService classificationService,
	                                          TransferService transferService,
	                                          PaymentsReportingService paymentsReportingService,
	                                          TransferClassificationService transferClassificationService,
	                                          TransferClassificationStoreService transferClassificationStoreService,
	                                          TreasuryService treasuryService,
	                                          InstallmentService installmentService,
	                                          PaymentNotificationService paymentNotificationService) {
		this.classificationService = classificationService;
		this.transferService = transferService;
		this.paymentsReportingService = paymentsReportingService;
		this.transferClassificationService = transferClassificationService;
		this.transferClassificationStoreService = transferClassificationStoreService;
        this.treasuryService = treasuryService;
		this.installmentService = installmentService;
		this.paymentNotificationService = paymentNotificationService;
	}

	@Override
	public void classify(TransferSemanticKeyDTO transferSemanticKey) {
		log.info("Transfer classification for organization id: {} and iuv: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv());

		// Clear previous classification
		Long deletedRowsNumber = classificationService.deleteBySemanticKey(transferSemanticKey);
		log.debug("Deleted {} classifications for organization id: {} and iuv: {}",
			deletedRowsNumber, transferSemanticKey.getOrgId(), transferSemanticKey.getIuv());

		// Retrieve Transfer 2 classify
		Transfer transferDTO = transferService.findBySemanticKey(transferSemanticKey, INSTALLMENT_PAYED_STATUSES_SET);

		// find Installment related to the transfer
		Optional<InstallmentNoPII> installmentDTO = findInstallment(transferDTO);

		// Retrieve related PaymentNotification
		PaymentNotificationNoPII paymentNotificationDTO = retrievePaymentNotification(transferSemanticKey.getOrgId(), installmentDTO);

		// Retrieve related PaymentsReporting
		log.info("Retrieve payment reporting for organization id: {} and iuv: {} and iur {} and transfer index: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex());
		PaymentsReporting paymentsReporting = paymentsReportingService.getBySemanticKey(transferSemanticKey);

		// Retrieve related Treasury
		TreasuryIuf treasuryIUF = retrieveTreasuryIuf(transferSemanticKey.getOrgId(), paymentsReporting);

		// Classify
		List<ClassificationsEnum> classifications = transferClassificationService.defineLabels(transferDTO, paymentNotificationDTO, paymentsReporting, treasuryIUF, installmentDTO);
		log.info("Labels defined for organization id: {} and iuv: {} and iur {} and transfer index: {} are: {}",
			transferSemanticKey.getOrgId(), transferSemanticKey.getIuv(), transferSemanticKey.getIur(), transferSemanticKey.getTransferIndex(),
			String.join(", ", classifications.stream().map(String::valueOf).toList()));

		// Store results
		transferClassificationStoreService.saveClassifications(transferSemanticKey, transferDTO, paymentsReporting, treasuryIUF, classifications);
		notifyReportedTransferId(transferDTO, paymentsReporting);
	}

	/**
	 * Retrieves the {@link TreasuryIuf} record for the given ID.
	 *
	 * @param orgId the ID of the organization
	 * @param paymentsReportingDTO the payments reporting data transfer object containing payment reporting details
	 * @return the {@link TreasuryIuf} corresponding to the given ID
	 */
	private TreasuryIuf retrieveTreasuryIuf(Long orgId, PaymentsReporting paymentsReportingDTO) {
		if (paymentsReportingDTO != null) {
			String iuf = paymentsReportingDTO.getIuf();
			log.info("Retrieve treasury for organization id: {} and iuf {}", orgId, iuf);
			return treasuryService.getByOrganizationIdAndIuf(orgId, iuf);
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
			transferService.notifyReportedTransferId(transferDTO.getTransferId(), new TransferReportedRequest(paymentsReportingDTO.getIuf()));
		}
	}

	/**
	 * Retrieves the {@link PaymentNotificationNoPII} record for the given organization ID and transfer.
	 *
	 * @param orgId the ID of the organization
	 * @param installmentDTO the installment data transfer object containing installment details
	 * @return the {@link PaymentNotificationNoPII} corresponding to the given organization ID and transfer
	 */
	private PaymentNotificationNoPII retrievePaymentNotification(Long orgId, Optional<InstallmentNoPII> installmentDTO) {
		return installmentDTO.map(installment -> {
				log.info("Retrieving payment notification from organizationId {} and iud", orgId, installment.getIud());
				return paymentNotificationService.getByOrgIdAndIud(orgId, installment.getIud());
			})
			.orElse(null);
	}

	/**
	 * Retrieves the {@link InstallmentNoPII} record for the given transfer.
	 *
	 * @param transferDTO the transfer data transfer object containing transfer details
	 * @return an {@link Optional} containing the {@link InstallmentNoPII} corresponding to the given transfer, or empty if not found
	 */
	private Optional<InstallmentNoPII> findInstallment(Transfer transferDTO) {
		return Optional.ofNullable(transferDTO)
			.map(Transfer::getInstallmentId)
			.flatMap(installmentService::getInstallmentById);
	}
}
