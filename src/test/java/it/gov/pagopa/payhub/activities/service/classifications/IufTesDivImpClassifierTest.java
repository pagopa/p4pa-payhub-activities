package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IufTesDivImpClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	LabelClassifier classifier = new IufTesDivImpClassifier();

	@Test
	void whenDefineThenSuccess() {
		// Act
		ClassificationsEnum result = classifier.define(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.IUF_TES_DIV_IMP, result);
	}

	@Test
	void whenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.define(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}
}