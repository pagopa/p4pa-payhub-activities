package it.gov.pagopa.payhub.activities.activity.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.utility.UserAuthorizationActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationActivityTest {
	@Mock
	private UserAuthorizationActivity userAuthorizationActivity;

	@BeforeEach
	void init() {
	}

	@Test
	void testUser() {
		String mappedExternalUserId = "TEST_USER";
		UserInfoDTO userInfoDTO = new UserInfoDTO();
		userInfoDTO.setMappedExternalUserId(mappedExternalUserId);
		when(userAuthorizationActivity.getUserInfo(mappedExternalUserId))
				.thenReturn(userInfoDTO);

		assertEquals(mappedExternalUserId, userInfoDTO.getMappedExternalUserId());
	}
}