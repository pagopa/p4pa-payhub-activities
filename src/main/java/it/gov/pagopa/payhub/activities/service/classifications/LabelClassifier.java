package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

import java.util.Optional;

public interface LabelClassifier {

	Optional<ClassificationsEnum> define(
		Optional<TransferDTO> transferDTO,
		Optional<PaymentsReportingDTO> paymentsReportingDTO,
		Optional<TreasuryDTO> treasuryDTO
	);
}
