package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.trclassifiers.TransferClassifier;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service for classifying a transfer based on receipt, payment reporting, and treasury data.
 * It uses a list of {@link TransferClassifier} to determine the corresponding labels.
 */
@Lazy
@Slf4j
@Service
public class TransferClassificationService {
	private final List<ClassificationsEnum> defaultClassification = List.of(ClassificationsEnum.UNKNOWN);
	private final List<TransferClassifier> classifiers;

	public TransferClassificationService(List<TransferClassifier> classifiers) {
		this.classifiers = classifiers;
	}

	/**
	 * Determines a list of labels ({@link ClassificationsEnum}) based on the provided data.
	 *
	 * For each classifier in the list, the {@code define} method is executed with the given parameters.
	 * Non-{@code null} labels are collected into a list. If no classifier produces a valid label,
	 * a {@link ClassificationException} is thrown.
	 *
	 * @param transferDTO the transfer data.
	 * @param paymentsReportingDTO the payment reporting data.
	 * @param treasuryDTO the treasury data.
	 * @return a list of valid labels generated by the classifiers.
	 * @throws ClassificationException if no valid label is determined.
	 */
	public List<ClassificationsEnum> defineLabels(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO) {
		List<ClassificationsEnum> labels = classifiers.stream()
			.map(classifier -> classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO))
			.filter(Objects::nonNull)
			.toList();

		if (labels.isEmpty()) {
			return defaultClassification;
		}
		return labels;
	}
}
