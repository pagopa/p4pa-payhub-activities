package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferClassificationActivityImplTest {
	private static final Long ORGANIZATION = 123L;
	private static final String IUV = "01011112222333345";
	private static final String IUR = "IUR";
	private static final String IUF = "IUF";
	private static final int INDEX = 1;
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final TreasuryIuf treasuryIuf = TreasuryFaker.buildTreasuryIuf();
	private final Set<InstallmentStatus> installmentStatusSet = Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

	@Mock
	private ClassificationService classificationServiceMock;

	@Mock
	private TransferService transferServiceMock;

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
				classificationServiceMock, transferServiceMock, paymentsReportingServiceMock, transferClassificationServiceMock, transferClassificationStoreServiceMock, treasuryServiceMock);
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
				classificationServiceMock, transferServiceMock, paymentsReportingServiceMock, treasuryServiceMock, transferClassificationServiceMock, transferClassificationStoreServiceMock);
	}

	@Test
	void classificationSuccess() {
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1L);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(paymentsReportingDTO);
		when(treasuryServiceMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenReturn(treasuryIuf);
		when(transferClassificationServiceMock.defineLabels(transferDTO, paymentsReportingDTO, treasuryIuf, null)).thenReturn(List.of(ClassificationsEnum.RT_IUF_TES));
		doReturn(1).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, paymentsReportingDTO, treasuryIuf, List.of(ClassificationsEnum.RT_IUF_TES));
		when(transferServiceMock.notifyReportedTransferId(transferDTO.getTransferId(), new TransferReportedRequest(paymentsReportingDTO.getIuf()))).thenReturn(new DebtPositionDTO());

		assertDoesNotThrow(() -> activity.classify(transferSemanticKeyDTO));

		Mockito.verify(classificationServiceMock, Mockito.times(1)).deleteBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(transferServiceMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void whenPaymentsReportingIsEmptyShouldNotCallTreasury() {
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1L);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(transferDTO);
		when(paymentsReportingServiceMock.getBySemanticKey(transferSemanticKeyDTO)).thenReturn(null);
		when(transferClassificationServiceMock.defineLabels(transferDTO, null, null, null)).thenReturn(List.of(ClassificationsEnum.RT_NO_IUF));
		doReturn(1).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, null, null, List.of(ClassificationsEnum.RT_NO_IUF));

		assertDoesNotThrow(() -> activity.classify(transferSemanticKeyDTO));

		Mockito.verify(classificationServiceMock, Mockito.times(1)).deleteBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(transferServiceMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

}
