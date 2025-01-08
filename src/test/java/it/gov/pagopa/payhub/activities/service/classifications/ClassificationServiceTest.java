package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	RtIufClassifier rtIufClassifierMock;

	@Mock
	RtIufTesClassifier rtIufTesClassifierMock;

	ClassificationService service;

	@Test
	void testDefineLabels_ReturnsLabels() {
		// Arrange
		service = new ClassificationService(List.of(rtIufClassifierMock, rtIufTesClassifierMock));

		when(rtIufTesClassifierMock.define(transferDTO, paymentsReportingDTO, treasuryDTO)).thenReturn(ClassificationsEnum.RT_IUF_TES);
		when(rtIufClassifierMock.define(transferDTO, paymentsReportingDTO, treasuryDTO)).thenReturn(ClassificationsEnum.RT_IUF);

		// Act
		List<ClassificationsEnum> labels = service.defineLabels(transferDTO, paymentsReportingDTO, treasuryDTO);

		// Assert
		assertEquals(2, labels.size());
		assertTrue(labels.contains(ClassificationsEnum.RT_IUF_TES));
		assertTrue(labels.contains(ClassificationsEnum.RT_IUF));
	}

	@Test
	void whenDefineLabelsThenThrowsClassificationException() {
		// Arrange
		service = new ClassificationService(List.of());
		// Act & Assert
		assertThrows(ClassificationException.class, () ->
			service.defineLabels(transferDTO, paymentsReportingDTO, treasuryDTO), "Cannot define classification");
	}
}