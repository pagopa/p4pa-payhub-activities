package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.dao.TransferDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import it.gov.pagopa.payhub.activities.exception.ClassificationException;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferClassificationActivityImplTest {
	private static final Long ORGANIZATION = 123L;
	private static final String IUV = "01011112222333345";
	private static final String IUR = "IUR";
	private static final String IUF = "IUF";
	private static final int INDEX = 1;
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final TransferDTO transferDTO = TransferFaker.buildTransferDTO();
	private final Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO();

	@Mock
	private ClassificationDao classificationDaoMock;

	@Mock
	private TransferDao transferDaoMock;

	@Mock
	private PaymentsReportingService paymentsReportingServiceMock;

	@Mock
	private TreasuryService treasuryServiceMock;

	@Mock
	private TransferClassificationService transferClassificationServiceMock;

	@Mock
	private TransferClassificationStoreService transferClassificationStoreServiceMock;

	private TransferSemanticKeyDTO transferSemanticKeyDTO;
	private TransferClassificationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new TransferClassificationActivityImpl(
				classificationDaoMock, transferDaoMock, paymentsReportingServiceMock, transferClassificationServiceMock, transferClassificationStoreServiceMock, treasuryServiceMock);
		transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(ORGANIZATION)
			.iuv(IUV)
			.iur(IUR)
			.transferIndex(INDEX)
			.build();
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				classificationDaoMock, transferDaoMock, paymentsReportingServiceMock, treasuryServiceMock, transferClassificationServiceMock, transferClassificationStoreServiceMock);
	}

	@Test
	void classificationSuccess() {
		ClassificationDTO classificationDTO = ClassificationDTO.builder()
			.organizationId(ORGANIZATION)
			.transferId(1L)
			.paymentReportingId("paymentsReportingId")
			.treasuryId("treasuryId")
			.iuf("IUF")
			.iuv(IUV)
			.iur(IUR)
			.transferIndex(INDEX)
			.classificationsEnum(ClassificationsEnum.RT_IUF_TES)
			.build();

		
		
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(paymentsReportingDTO);
		when(treasuryServiceMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenReturn(Optional.of(treasuryDTO));
		when(transferClassificationServiceMock.defineLabels(transferDTO, paymentsReportingDTO, treasuryDTO)).thenReturn(List.of(ClassificationsEnum.RT_IUF_TES));
		doReturn(List.of(classificationDTO)).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryDTO, List.of(ClassificationsEnum.RT_IUF_TES));
		when(transferDaoMock.notifyReportedTransferId(transferDTO.getTransferId())).thenReturn(true);

		assertDoesNotThrow(() -> activity.classify(transferSemanticKeyDTO));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedDeletePreviousClassificationWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.FALSE);
		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(0)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(0)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindTransferWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenThrow(new ClassificationException("retrieving failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(0)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindPaymentsReportingWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenThrow(new ClassificationException("payments reporting find failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void whenPaymentsReportingIsEmptyShouldNotCallTreasury() {
		ClassificationDTO classificationDTO = ClassificationDTO.builder()
			.organizationId(ORGANIZATION)
			.transferId(1L)
			.iuv(IUV)
			.iur(IUR)
			.transferIndex(INDEX)
			.classificationsEnum(ClassificationsEnum.RT_NO_IUF)
			.build();
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(null);
		when(transferClassificationServiceMock.defineLabels(transferDTO, null, null)).thenReturn(List.of(ClassificationsEnum.RT_NO_IUF));
		doReturn(List.of(classificationDTO)).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, null, null, List.of(ClassificationsEnum.RT_NO_IUF));

		assertDoesNotThrow(() -> activity.classify(transferSemanticKeyDTO));

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenFailedFindTreasuryWhenClassifyThenClassificationFailed() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(paymentsReportingDTO);
		when(treasuryServiceMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenThrow(new ClassificationException("treasury find failed"));
		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenErrorWhenDefineLabelsThenClassificationException() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(null);
		when(transferClassificationServiceMock.defineLabels(transferDTO, null, null))
			.thenThrow(new ClassificationException("Cannot define labels"));

		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");

		Mockito.verify(classificationDaoMock, Mockito.times(1)).deleteTransferClassification(transferSemanticKeyDTO);
		Mockito.verify(transferDaoMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void givenErrorWhenSaveClassificationsThenClassificationException() {
		when(classificationDaoMock.deleteTransferClassification(transferSemanticKeyDTO)).thenReturn(Boolean.TRUE);
		when(transferDaoMock.findBySemanticKey(transferSemanticKeyDTO)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(null);
		when(transferClassificationServiceMock.defineLabels(transferDTO, null, null)).thenReturn(List.of(ClassificationsEnum.RT_NO_IUF));
		doThrow(new ClassificationException("Error given while saving")).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, null, null, List.of(ClassificationsEnum.RT_NO_IUF));

		assertThrows(ClassificationException.class, () -> activity.classify(transferSemanticKeyDTO), "classification failed");
	}
}
