package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RtIufTesClassifierTest {
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	TransferClassifier classifier = new RtIufTesClassifier();

	@Test
	void givenMatchedConditionWhenDefineThenSuccess() {
		// Arrange
		transferDTO.setAmountCents(10000L);
		paymentsReportingDTO.setAmountPaidCents(10000L);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(100.00D));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertEquals(ClassificationsEnum.RT_IUF_TES, result);
	}

	@Test
	void givenUnmatchedTreasuryDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, null);
		// Assert
		assertNull(result);
	}

	@ParameterizedTest
	@CsvSource({
		"10000, 100.00",
		"100, 100.00",
		"1000, 1.00"
	})
	void givenUnmatchedAmountsWhenDefineThenReturnNull(Long paymentsReportingAmount, Double treasuryAmount) {
		// Arrange
		transferDTO.setAmountCents(100L);
		paymentsReportingDTO.setAmountPaidCents(paymentsReportingAmount);
		treasuryDTO.setBillIpNumber(BigDecimal.valueOf(treasuryAmount));
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedPaymentsReportingDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(transferDTO, null, treasuryDTO);
		// Assert
		assertNull(result);
	}

	@Test
	void givenUnmatchedTransferDTOWhenDefineThenReturnNull() {
		// Act
		ClassificationsEnum result = classifier.classify(null, paymentsReportingDTO, treasuryDTO);
		// Assert
		assertNull(result);
	}
}
