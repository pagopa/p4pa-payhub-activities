package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.LabelClassifier;
import it.gov.pagopa.payhub.activities.service.classifications.RtIufClassifier;
import it.gov.pagopa.payhub.activities.service.classifications.RtIufTesClassifier;
import it.gov.pagopa.payhub.activities.utility.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationActivityImplTest {
	private static final Long ORGANIZATION = 123L;
	private static final String IUV = "01011112222333345";
	private static final String IUR = "IUR";
	private static final String IUF = "IUF";
	private static final int INDEX = 1;
	private final PaymentsReportingDTO paymentsReportingDTO = PaymentsReportingFaker.buildClassifyResultDTO();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final TreasuryDTO treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	private ClassificationDao classificationDaoMock;

	@Mock
	private TransferDao transferDaoMock;

	@Mock
	private PaymentsReportingDao paymentsReportingDaoMock;

	@Mock
	private TreasuryDao treasuryDaoMock;

	@Mock
	private RtIufTesClassifier rtIufTesClassifierMock;

	@Mock
	private RtIufClassifier rtIufClassifierMock;

	private List<LabelClassifier> labelClassifiers = new ArrayList<>();
	private TransferClassificationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new TransferClassificationActivityImpl(
				classificationDaoMock, transferDaoMock, paymentsReportingDaoMock, treasuryDaoMock, labelClassifiers);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				classificationDaoMock, transferDaoMock, paymentsReportingDaoMock, treasuryDaoMock);
	}

	@Test
	void classificationSuccess() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(transferDTO);
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(paymentsReportingDTO);
		when(treasuryDaoMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenReturn(treasuryDTO);
		when(rtIufTesClassifierMock.define(transferDTO, paymentsReportingDTO, treasuryDTO)).thenReturn(ClassificationsEnum.RT_IUF_TES);
		labelClassifiers.add(rtIufTesClassifierMock);

		assertDoesNotThrow(() -> activity.classify(ORGANIZATION, IUV, IUR, INDEX));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedDeletePreviousClassificationWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.FALSE);
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindTransferWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenThrow(new ClassificationException("retrieving failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindPaymentsReportingWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(transferDTO);
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenThrow(new ClassificationException("payments reporting find failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void whenPaymentsReportingDTOIsEmptyShouldNotCallTreasury() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(transferDTO);
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(null);
		when(rtIufClassifierMock.define(transferDTO, null, null)).thenReturn(ClassificationsEnum.RT_NO_IUF);
		labelClassifiers.add(rtIufClassifierMock);

		assertDoesNotThrow(() -> activity.classify(ORGANIZATION, IUV, IUR, INDEX));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindTreasuryWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(transferDTO);
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(paymentsReportingDTO);
		when(treasuryDaoMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenThrow(new ClassificationException("treasury find failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

}
