package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.service.classifications.trclassifiers.RtIufClassifier;
import it.gov.pagopa.payhub.activities.service.classifications.trclassifiers.RtIufTesClassifier;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferTreasuryServiceTest {

	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final TreasuryIuf treasuryIUF = TreasuryFaker.buildTreasuryIuf();

	@Mock
	private RtIufClassifier rtIufClassifierMock;

	@Mock
	private RtIufTesClassifier rtIufTesClassifierMock;

	private TransferClassificationService service;

	@Test
	void whenDefineLabelsThenReturnsLabels() {
		// Arrange
		service = new TransferClassificationService(List.of(rtIufClassifierMock, rtIufTesClassifierMock));

		when(rtIufTesClassifierMock.classify(transferDTO, null, null, paymentsReportingDTO, treasuryIUF)).thenReturn(ClassificationsEnum.RT_IUF_TES);
		when(rtIufClassifierMock.classify(transferDTO, null, null, paymentsReportingDTO, treasuryIUF)).thenReturn(ClassificationsEnum.RT_IUF);

		// Act
		List<ClassificationsEnum> labels = service.defineLabels(transferDTO, null, null, paymentsReportingDTO, treasuryIUF);

		// Assert
		assertEquals(2, labels.size());
		assertTrue(labels.contains(ClassificationsEnum.RT_IUF_TES));
		assertTrue(labels.contains(ClassificationsEnum.RT_IUF));
	}

	@Test
	void whenDefineLabelsThenReturnsDefaultLabel() {
		// Arrange
		service = new TransferClassificationService(List.of());
		// Act
		List<ClassificationsEnum> labels = service.defineLabels(transferDTO, null, null, paymentsReportingDTO, treasuryIUF);

		// Assert
		assertEquals(1, labels.size());
		assertTrue(labels.contains(ClassificationsEnum.UNKNOWN));
	}
}