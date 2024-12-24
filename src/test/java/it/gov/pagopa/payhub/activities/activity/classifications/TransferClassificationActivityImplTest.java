package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationActivityImplTest {
	private static final Long ORGANIZATION = 123L;
	private static final String IUV = "01011112222333345";
	private static final String IUR = "IUR";
	private static final int INDEX = 1;

	@Mock
	private ClassificationDao classificationDaoMock;

	@Mock
	private TransferDao transferDaoMock;

	@Mock
	private PaymentsReportingDao paymentsReportingDaoMock;

	@Mock
	private TreasuryDao treasuryDaoMock;

	private TransferClassificationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new TransferClassificationActivityImpl(
				classificationDaoMock, transferDaoMock, paymentsReportingDaoMock, treasuryDaoMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				classificationDaoMock, transferDaoMock, paymentsReportingDaoMock, treasuryDaoMock);
	}

	@Test
	void classificationSuccess() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(new TransferDTO());
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(new PaymentsReportingDTO());
		when(treasuryDaoMock.getByOrganizationIdAndIur(ORGANIZATION, IUR)).thenReturn(new TreasuryDTO());

		assertDoesNotThrow(() -> activity.classify(ORGANIZATION, IUV, IUR, INDEX));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(1)).getByOrganizationIdAndIur(ORGANIZATION, IUR);
	}

	@Test
	void givenFailedDeletePreviousClassificationWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.FALSE);
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIur(ORGANIZATION, IUR);
	}

	@Test
	void givenFailedFindTransferWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenThrow(new ClassificationException("retrieving failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(0)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIur(ORGANIZATION, IUR);
	}

	@Test
	void givenFailedFindPaymentsReportingWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(new TransferDTO());
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenThrow(new ClassificationException("payments reporting find failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(ORGANIZATION, IUV, IUR, INDEX), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIur(ORGANIZATION, IUR);
	}

	@Test
	void whenPaymentsReportingDTOIsNullShouldNotCallTreasury() {
		when(classificationDaoMock.deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(new TransferDTO());
		when(paymentsReportingDaoMock.findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX)).thenReturn(null);
		assertDoesNotThrow(() -> activity.classify(ORGANIZATION, IUV, IUR, INDEX));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(paymentsReportingDaoMock, Mockito.times(1)).findBySemanticKey(ORGANIZATION, IUV, IUR, INDEX);
		Mockito.verify(treasuryDaoMock, Mockito.times(0)).getByOrganizationIdAndIur(ORGANIZATION, IUR);
	}

}
