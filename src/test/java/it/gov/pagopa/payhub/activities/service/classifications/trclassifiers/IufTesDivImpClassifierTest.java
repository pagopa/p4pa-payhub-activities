package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IufTesDivImpClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new IufTesDivImpClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		paymentsReportingDTO.setTotalAmountCents(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(1.00D));
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.IUF_TES_DIV_IMP, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedAmountWhenDefineThenReturnNull() {
		// Arrange
		paymentsReportingDTO.setTotalAmountCents(100L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(1.00D));
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
