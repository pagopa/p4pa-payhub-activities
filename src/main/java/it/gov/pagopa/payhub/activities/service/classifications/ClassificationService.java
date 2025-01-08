package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Lazy
@Slf4j
@Service
public class ClassificationService {
	private final List<LabelClassifier> classifiers;

	public ClassificationService(List<LabelClassifier> classifiers) {
		this.classifiers = classifiers;
	}

	public List<ClassificationsEnum> defineLabels(TransferDTO transferDTO, PaymentsReportingDTO paymentsReportingDTO, TreasuryDTO treasuryDTO) {
		List<ClassificationsEnum> labels = classifiers.stream()
			.map(classifier -> classifier.define(transferDTO, paymentsReportingDTO, treasuryDTO))
			.filter(Objects::nonNull)
			.toList();

		if (labels.isEmpty()) {
			throw new ClassificationException("Cannot define classification");
		}
		return labels;
	}
}
