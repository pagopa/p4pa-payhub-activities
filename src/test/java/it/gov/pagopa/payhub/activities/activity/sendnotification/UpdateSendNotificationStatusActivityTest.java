package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.SendService;
import it.gov.pagopa.payhub.activities.exception.sendnotification.SendNotificationNotFoundException;
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
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class UpdateSendNotificationStatusActivityTest {

	@Mock
	private SendService sendServiceMock;
	@Mock
	private InstallmentService installmentServiceMock;

	private UpdateSendNotificationStatusActivity updateSendNotificationStatusActivity;

	@BeforeEach
	void init() {
		updateSendNotificationStatusActivity = new UpdateSendNotificationStatusActivityImpl(
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

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(expectedResponse);
		Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(expectedResponse);

		// When
		SendNotificationDTO result = updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId);

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

		Mockito.doThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null))
				.when(sendServiceMock)
				.retrieveNotificationByNotificationRequestId(notificationRequestId);

		// When
		SendNotificationNotFoundException notRetryableActivityException = Assertions.assertThrows(
				SendNotificationNotFoundException.class,
				() -> updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId)
		);

		// Then
		Assertions.assertNotNull(notRetryableActivityException);
		Assertions.assertEquals(
				"Notification for notificationRequestId %s not found".formatted(notificationRequestId),
				notRetryableActivityException.getMessage()
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

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(sendNotificationDTO);

		// When
		SendNotificationDTO result = updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId);

		// Then
		assertSame(sendNotificationDTO, result);
		Mockito.verify(sendServiceMock).notificationStatus(notificationId);
		Mockito.verify(installmentServiceMock).updateIunByDebtPositionId(debtPositionId, iun);
	}

	@Test
	void givenSendNotificationDTOIsReturnedNullByRetrieveNotificationByNotificationRequestIdWhenSendNotificationStatusThenDoNothing() {
		// Given
		String notificationId = "sendNotificationId";
		String notificationRequestId = "notificationRequestId";

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(null);

		// When
		SendNotificationDTO result = updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId);

		// Then
		assertNull(result);
		Mockito.verify(sendServiceMock).retrieveNotificationByNotificationRequestId(notificationRequestId);
		Mockito.verify(sendServiceMock, Mockito.times(0))
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

		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(null);

		// When
		SendNotificationDTO result = updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId);

		// Then
		assertNull(result);
		Mockito.verify(sendServiceMock).retrieveNotificationByNotificationRequestId(notificationRequestId);
		Mockito.verify(sendServiceMock).notificationStatus(notificationId);
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
		Mockito.when(sendServiceMock.retrieveNotificationByNotificationRequestId(notificationRequestId)).thenReturn(sendNotificationDTO);
		Mockito.when(sendServiceMock.notificationStatus(notificationId)).thenReturn(sendNotificationDTO);
		// When
		SendNotificationDTO result = updateSendNotificationStatusActivity.updateSendNotificationStatus(notificationRequestId);
		// Then
		assertSame(sendNotificationDTO, result);
		Mockito.verify(sendServiceMock).notificationStatus(notificationId);
		Mockito.verifyNoInteractions(installmentServiceMock);
	}

}