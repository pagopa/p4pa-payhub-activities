package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.PaymentsReporting2ReceiptMapper;
import it.gov.pagopa.payhub.activities.util.DebtPositionUtilities;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingImplicitReceiptHandlerActivityImplTest {
	@Mock
	private PaymentsReportingService paymentsReportingServiceMock;
	@Mock
	private OrganizationService organizationServiceMock;
	@Mock
	private PaymentsReporting2ReceiptMapper paymentsReporting2ReceiptMapperMock;
	@Mock
	private ReceiptService receiptServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;

	private PaymentsReportingImplicitReceiptHandlerActivity activity;

	@BeforeEach
	void setUp() {
		activity = new PaymentsReportingImplicitReceiptHandlerActivityImpl(
			paymentsReportingServiceMock,
			organizationServiceMock,
			paymentsReporting2ReceiptMapperMock,
			receiptServiceMock,
            installmentServiceMock
		);
	}

	@Test
	void whenHandleImplicitReceiptThenCompleteSuccessfully() {
		// Given
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = mock(PaymentsReportingTransferDTO.class);
		when(paymentsReportingTransferDTO.getPaymentOutcomeCode()).thenReturn("9");
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
		Organization organizationFake = OrganizationFaker.buildOrganizationDTO();
		ReceiptWithAdditionalNodeDataDTO dummyReceiptMocked = mock(ReceiptWithAdditionalNodeDataDTO.class);
		ReceiptDTO dummyReceiptCreated = mock(ReceiptDTO.class);

        InstallmentDebtorDTO installmentDebtorDTO = mock(InstallmentDebtorDTO.class);
        installmentDebtorDTO.setIuv(paymentsReportingFake.getIuv());
        installmentDebtorDTO.setOrganizationId(organizationFake.getOrganizationId());

        when(installmentServiceMock.findByIuvOrNav(paymentsReportingFake.getIuv(), null, organizationFake.getOrganizationId(),  DebtPositionUtilities.UNPAID_OR_PAID_INSTALLMENT_STATUSES_LIST)).thenReturn(List.of(installmentDebtorDTO));
		when(paymentsReportingServiceMock.getByTransferSemanticKey(paymentsReportingTransferDTO)).thenReturn(paymentsReportingFake);
		when(organizationServiceMock.getOrganizationById(paymentsReportingFake.getOrganizationId())).thenReturn(Optional.of(organizationFake));
		when(paymentsReporting2ReceiptMapperMock.map2Receipt(paymentsReportingFake, organizationFake, List.of(installmentDebtorDTO))).thenReturn(dummyReceiptMocked);
		when(receiptServiceMock.createReceipt(dummyReceiptMocked)).thenReturn(dummyReceiptCreated);

		// When
		activity.handleImplicitReceipt(paymentsReportingTransferDTO);

		// Then
		verify(paymentsReportingServiceMock, times(1)).getByTransferSemanticKey(paymentsReportingTransferDTO);
		verify(organizationServiceMock, times(1)).getOrganizationById(paymentsReportingFake.getOrganizationId());
		verify(paymentsReporting2ReceiptMapperMock, times(1)).map2Receipt(paymentsReportingFake, organizationFake, List.of(installmentDebtorDTO));
		verify(receiptServiceMock, times(1)).createReceipt(dummyReceiptMocked);
	}

	@Test
	void givenInvalidOrgIdWhenHandleImplicitReceiptThenThrowsException() {
		// Given
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = mock(PaymentsReportingTransferDTO.class);
		when(paymentsReportingTransferDTO.getPaymentOutcomeCode()).thenReturn("8");
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();

		when(paymentsReportingServiceMock.getByTransferSemanticKey(paymentsReportingTransferDTO)).thenReturn(paymentsReportingFake);
		when(organizationServiceMock.getOrganizationById(paymentsReportingFake.getOrganizationId()))
			.thenThrow(InvalidValueException.class);

		// When Then
		assertThrows(InvalidValueException.class, () -> activity.handleImplicitReceipt(paymentsReportingTransferDTO), "invalid");
	}

	@Test
	void givenPaymentOutcomeCodeNotInListWhenHandleImplicitReceiptThenDoesNotCreateReceipt() {
		// Given
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = mock(PaymentsReportingTransferDTO.class);
		when(paymentsReportingTransferDTO.getPaymentOutcomeCode()).thenReturn("0");

		// When
		activity.handleImplicitReceipt(paymentsReportingTransferDTO);

		// Then
		verify(paymentsReportingServiceMock, never()).getByTransferSemanticKey(paymentsReportingTransferDTO);
		verify(organizationServiceMock, never()).getOrganizationById(any());
		verify(paymentsReporting2ReceiptMapperMock, never()).map2Receipt(any(), any(), any());
		verify(receiptServiceMock, never()).createReceipt(any());
	}
}
