package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.PaymentsReporting2ReceiptMapper;
import it.gov.pagopa.payhub.activities.util.faker.OrganizationFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

	private PaymentsReportingImplicitReceiptHandlerActivity activity;

	@BeforeEach
	void setUp() {
		activity = new PaymentsReportingImplicitReceiptHandlerActivityImpl(
			paymentsReportingServiceMock,
			organizationServiceMock,
			paymentsReporting2ReceiptMapperMock,
			receiptServiceMock
		);
	}

	@Test
	void whenHandleThenCompleteSuccessfully() {
		// Given
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = mock(PaymentsReportingTransferDTO.class);
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();
		Organization organizationFake = OrganizationFaker.buildOrganizationDTO();
		ReceiptWithAdditionalNodeDataDTO dummyReceiptMocked = mock(ReceiptWithAdditionalNodeDataDTO.class);
		ReceiptDTO dummyReceiptCreated = mock(ReceiptDTO.class);

		when(paymentsReportingServiceMock.getBySemanticKey(paymentsReportingTransferDTO)).thenReturn(paymentsReportingFake);
		when(organizationServiceMock.getOrganizationById(paymentsReportingFake.getOrganizationId())).thenReturn(Optional.of(organizationFake));
		when(paymentsReporting2ReceiptMapperMock.map2DummyReceipt(paymentsReportingFake, organizationFake.getOrgFiscalCode())).thenReturn(dummyReceiptMocked);
		when(receiptServiceMock.createReceipt(dummyReceiptMocked)).thenReturn(dummyReceiptCreated);

		// When Then
		assertDoesNotThrow(() -> activity.handle(paymentsReportingTransferDTO));

		verify(paymentsReportingServiceMock, times(1)).getBySemanticKey(paymentsReportingTransferDTO);
		verify(organizationServiceMock, times(1)).getOrganizationById(paymentsReportingFake.getOrganizationId());
		verify(paymentsReporting2ReceiptMapperMock, times(1)).map2DummyReceipt(paymentsReportingFake, organizationFake.getOrgFiscalCode());
		verify(receiptServiceMock, times(1)).createReceipt(dummyReceiptMocked);
	}

	@Test
	void givenInvalidOrgIdWhenHandleThenThrowsException() {
		// Given
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = mock(PaymentsReportingTransferDTO.class);
		PaymentsReporting paymentsReportingFake = PaymentsReportingFaker.buildPaymentsReporting();

		when(paymentsReportingServiceMock.getBySemanticKey(paymentsReportingTransferDTO)).thenReturn(paymentsReportingFake);
		when(organizationServiceMock.getOrganizationById(paymentsReportingFake.getOrganizationId()))
			.thenThrow(InvalidValueException.class);

		// When Then
		assertThrows(InvalidValueException.class, () -> activity.handle(paymentsReportingTransferDTO), "invalid");
	}
}