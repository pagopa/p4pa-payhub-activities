package it.gov.pagopa.payhub.activities.activity.sendnotification;

import it.gov.pagopa.payhub.activities.connector.sendnotification.SendNotificationService;
import it.gov.pagopa.pu.sendnotification.dto.generated.ProgressResponseElementV25DTO;
import it.gov.pagopa.pu.sendnotification.dto.generated.SendStreamDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GetSendStreamActivityTest {

	@Mock
	private SendNotificationService serviceMock;

	private GetSendStreamActivity activity;

	@BeforeEach
	void init(){
		this.activity = new GetSendStreamActivityImpl(serviceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void testFetchSendStream() {
		// Given
		Long organizationId = 1L;

		SendStreamDTO expectedResult = new SendStreamDTO();

		Mockito.when(serviceMock.findSendStream(organizationId))
				.thenReturn(expectedResult);

		// When
		SendStreamDTO actualResult = activity.fetchSendStream(organizationId);

		// Then
		Assertions.assertSame(expectedResult, actualResult);
	}
}