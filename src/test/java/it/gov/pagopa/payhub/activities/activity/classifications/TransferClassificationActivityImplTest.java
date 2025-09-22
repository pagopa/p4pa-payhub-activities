package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationService;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
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
	private final InstallmentNoPII installmentDTO = TestUtils.getPodamFactory().manufacturePojo(InstallmentNoPII.class);
	private final PaymentNotificationNoPII paymentNotificationDTO = TestUtils.getPodamFactory().manufacturePojo(PaymentNotificationNoPII.class);
	private final PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting();
	private final Transfer transferDTO = TransferFaker.buildTransfer();
	private final TreasuryIuf treasuryIuf = TreasuryFaker.buildTreasuryIuf();
	private final Set<InstallmentStatus> installmentStatusSet = Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);
	private final Organization organization = TestUtils.getPodamFactory().manufacturePojo(Organization.class);

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
	@Mock
	private PaymentNotificationService paymentNotificationServiceMock;
	@Mock
	private InstallmentService installmentServiceMock;
	@Mock
	private OrganizationService organizationServiceMock;

	private TransferSemanticKeyDTO transferSemanticKeyDTO;
	private TransferClassificationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new TransferClassificationActivityImpl(
			classificationServiceMock,
			transferServiceMock,
			paymentsReportingServiceMock,
			transferClassificationServiceMock,
			transferClassificationStoreServiceMock,
			treasuryServiceMock,
			installmentServiceMock,
			paymentNotificationServiceMock,
			organizationServiceMock);
		transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
			.orgId(ORGANIZATION)
			.iuv(IUV)
			.iur(IUR)
			.transferIndex(INDEX)
			.build();
		organization.setOrganizationId(ORGANIZATION);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				classificationServiceMock,
				transferServiceMock,
				paymentsReportingServiceMock,
				transferClassificationServiceMock,
				transferClassificationStoreServiceMock,
				treasuryServiceMock,
				installmentServiceMock,
				paymentNotificationServiceMock,
				organizationServiceMock);
	}

	@Test
	void classificationSuccess() {
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(transferDTO);
		when(installmentServiceMock.getInstallmentById(transferDTO.getInstallmentId())).thenReturn(Optional.ofNullable(installmentDTO));
		when(paymentNotificationServiceMock.getByOrgIdAndIud(transferSemanticKeyDTO.getOrgId(), installmentDTO.getIud())).thenReturn(paymentNotificationDTO);
		when(paymentsReportingServiceMock.getByTransferSemanticKey(transferSemanticKeyDTO)).thenReturn(paymentsReportingDTO);
		when(treasuryServiceMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenReturn(treasuryIuf);
		when(organizationServiceMock.getOrganizationByFiscalCode(transferDTO.getOrgFiscalCode())).thenReturn(Optional.of(organization));
		when(transferClassificationServiceMock.defineLabels(transferDTO, installmentDTO, paymentNotificationDTO, paymentsReportingDTO, treasuryIuf)).thenReturn(List.of(ClassificationsEnum.RT_IUF_TES));
		doReturn(1).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, installmentDTO, paymentsReportingDTO, treasuryIuf, paymentNotificationDTO, List.of(ClassificationsEnum.RT_IUF_TES));
		when(transferServiceMock.notifyReportedTransferId(transferDTO.getTransferId(), new TransferReportedRequest(paymentsReportingDTO.getIuf()))).thenReturn(new DebtPositionDTO());

		assertDoesNotThrow(() -> activity.classifyTransfer(transferSemanticKeyDTO));

		Mockito.verify(classificationServiceMock, Mockito.times(1)).deleteBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(transferServiceMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getByTransferSemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(1)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void whenPaymentsReportingIsEmptyShouldNotCallTreasury() {
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(transferDTO);
		when(installmentServiceMock.getInstallmentById(transferDTO.getInstallmentId())).thenReturn(Optional.ofNullable(installmentDTO));
		when(paymentNotificationServiceMock.getByOrgIdAndIud(transferSemanticKeyDTO.getOrgId(), installmentDTO.getIud())).thenReturn(paymentNotificationDTO);
		when(paymentsReportingServiceMock.getByTransferSemanticKey(transferSemanticKeyDTO)).thenReturn(null);
		when(organizationServiceMock.getOrganizationByFiscalCode(transferDTO.getOrgFiscalCode())).thenReturn(Optional.of(organization));
		when(transferClassificationServiceMock.defineLabels(transferDTO, installmentDTO, paymentNotificationDTO, null, null)).thenReturn(List.of(ClassificationsEnum.RT_NO_IUF));
		doReturn(1).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, transferDTO, installmentDTO, null, null, paymentNotificationDTO, List.of(ClassificationsEnum.RT_NO_IUF));

		assertDoesNotThrow(() -> activity.classifyTransfer(transferSemanticKeyDTO));

		Mockito.verify(classificationServiceMock, Mockito.times(1)).deleteBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(transferServiceMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(1)).getByTransferSemanticKey(transferSemanticKeyDTO);
		Mockito.verify(treasuryServiceMock, Mockito.times(0)).getByOrganizationIdAndIuf(ORGANIZATION, IUF);
	}

	@Test
	void whenTransferIsNullShouldNotCallPaymentNotification() {
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(null);
		when(paymentsReportingServiceMock.getByTransferSemanticKey(transferSemanticKeyDTO)).thenReturn(paymentsReportingDTO);
		when(treasuryServiceMock.getByOrganizationIdAndIuf(ORGANIZATION, IUF)).thenReturn(treasuryIuf);
		when(transferClassificationServiceMock.defineLabels(null, null, null, paymentsReportingDTO, treasuryIuf)).thenReturn(List.of(ClassificationsEnum.IUV_NO_RT));
		doReturn(1).when(transferClassificationStoreServiceMock)
			.saveClassifications(transferSemanticKeyDTO, null, null, paymentsReportingDTO, treasuryIuf, null, List.of(ClassificationsEnum.IUV_NO_RT));

		assertDoesNotThrow(() -> activity.classifyTransfer(transferSemanticKeyDTO));
	}

	@Test
	void whenTransferHasDifferentOriginThenNotCreateClassification() {
		organization.setOrganizationId(999L);
		when(classificationServiceMock.deleteBySemanticKey(transferSemanticKeyDTO)).thenReturn(1);
		when(transferServiceMock.findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet)).thenReturn(transferDTO);
		when(organizationServiceMock.getOrganizationByFiscalCode(transferDTO.getOrgFiscalCode())).thenReturn(Optional.of(organization));

		assertDoesNotThrow(() -> activity.classifyTransfer(transferSemanticKeyDTO));

		Mockito.verify(classificationServiceMock, Mockito.times(1)).deleteBySemanticKey(transferSemanticKeyDTO);
		Mockito.verify(transferServiceMock, Mockito.times(1)).findBySemanticKey(transferSemanticKeyDTO, installmentStatusSet);
		Mockito.verify(paymentsReportingServiceMock, Mockito.times(0)).getByTransferSemanticKey(transferSemanticKeyDTO);}
}
