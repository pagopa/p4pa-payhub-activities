package it.gov.pagopa.payhub.activities.activity.sendnotification.stream.processing;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendStreamSkippedEventException;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationDTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationPaymentsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateSendNotificationStatusActivityTest {

	@Mock
	private SendService sendServiceMock;
	@Mock
	private InstallmentService installmentServiceMock;

	private ValidateSendNotificationStatusActivity validateSendNotificationStatusActivity;

	@BeforeEach
	void init() {
		validateSendNotificationStatusActivity = new ValidateSendNotificationStatusActivityImpl(
				sendServiceMock,
				installmentServiceMock
		);
	}

	@Test
	void whenSendNotificationStatusThenOk() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";
		SendNotificationDTO expectedResponse = new SendNotificationDTO();
		expectedResponse.setSendNotificationId(notificationId);

		when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(expectedResponse);
		when(sendServiceMock.notificationStatus(notificationId)).thenReturn(expectedResponse);

		// When
		SendNotificationDTO result = validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId);

		// Then
		assertSame(expectedResponse, result);
	}

	@Test
	void givenNotFoundExceptionWhenSendNotificationStatusThenThrowNotRetryableActivityException() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";
		SendNotificationDTO expectedResponse = new SendNotificationDTO();
		expectedResponse.setSendNotificationId(notificationId);

		doThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"))
				.when(sendServiceMock)
				.retrieveNotificationByNotificationRequestId(notificationRequestId);

		// When
		SendStreamSkippedEventException sendStreamSkippedEventException = Assertions.assertThrows(
				SendStreamSkippedEventException.class,
				() -> validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId)
		);

		// Then
		Assertions.assertNotNull(sendStreamSkippedEventException);
		String causeErrorMessage = "Notification for notificationRequestId %s not found: error message ERRORMESSAGE".formatted(notificationRequestId);
		Assertions.assertEquals(
				"Skipped an error during execution of activity %s: %s".formatted(ValidateSendNotificationStatusActivity.class.getSimpleName(), causeErrorMessage),
			sendStreamSkippedEventException.getMessage()
		);
	}

	@Test
	void givenAllDataPresentWhenSendNotificationStatusThenVerifyUpdatesInstallmentIun() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";
		String iun = "IUN";
		Long debtPositionId = 1L;

		SendNotificationPaymentsDTO notificationPayment = new SendNotificationPaymentsDTO();
		notificationPayment.setDebtPositionId(debtPositionId);

		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(notificationId);
		sendNotificationDTO.setIun(iun);
		sendNotificationDTO.setPayments(List.of(notificationPayment));

		when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		when(sendServiceMock.notificationStatus(notificationId)).thenReturn(sendNotificationDTO);

		// When
		SendNotificationDTO result = validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId);

		// Then
		assertSame(sendNotificationDTO, result);
		verify(sendServiceMock).notificationStatus(notificationId);
		verify(installmentServiceMock).updateIunByDebtPositionId(debtPositionId, iun);
	}

	@Test
	void givenSendNotificationDTOIsReturnedNullByRetrieveNotificationByNotificationRequestIdWhenSendNotificationStatusThenDoNothing() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";

		when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(null);

		// When
		SendNotificationDTO result = validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId);

		// Then
		assertNull(result);
		verify(sendServiceMock).retrieveNotificationByNotificationRequestId(notificationRequestId);
		verify(sendServiceMock, times(0))
				.retrieveNotificationByNotificationRequestId(notificationId);
		Mockito.verifyNoInteractions(installmentServiceMock);
	}

	@Test
	void givenSendNotificationDTOIsReturnedNullByNotificationStatusWhenSendNotificationStatusThenDoNothing() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";
		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(notificationId);

		when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		when(sendServiceMock.notificationStatus(notificationId)).thenReturn(null);

		// When
		SendNotificationDTO result = validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId);

		// Then
		assertNull(result);
		verify(sendServiceMock).retrieveNotificationByNotificationRequestId(notificationRequestId);
		verify(sendServiceMock).notificationStatus(notificationId);
		Mockito.verifyNoInteractions(installmentServiceMock);
	}

	@Test
	void givenIunIsNullWhenSendNotificationStatusThenDoNothing() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";
		SendNotificationDTO sendNotificationDTO = new SendNotificationDTO();
		sendNotificationDTO.setSendNotificationId(notificationId);
		sendNotificationDTO.setIun(null);
		when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		when(sendServiceMock.notificationStatus(notificationId)).thenReturn(sendNotificationDTO);
		// When
		SendNotificationDTO result = validateSendNotificationStatusActivity.validateSendNotificationStatus(notificationRequestId);
		// Then
		assertSame(sendNotificationDTO, result);
		verify(sendServiceMock).notificationStatus(notificationId);
		Mockito.verifyNoInteractions(installmentServiceMock);
	}

}