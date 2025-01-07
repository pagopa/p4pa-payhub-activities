package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Lazy
@Component
public class IufTesDivImpClassifier implements LabelClassifier {
	@Override
	public Optional<ClassificationsEnum> define(Optional<TransferDTO> transferDTO, Optional<PaymentsReportingDTO> paymentsReportingDTO, Optional<TreasuryDTO> treasuryDTO) {
		return Optional.empty();
	}
}
