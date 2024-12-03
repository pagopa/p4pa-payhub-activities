package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceTest {
	@Mock
	private UserAuthorizationServiceImpl userAuthorizationService;

	@BeforeEach
	void setup() {
		userAuthorizationService = new UserAuthorizationServiceImpl();
	}

	@Test
	void testUser() {
		String mappedExternalUserId = "USER";
		UserInfoDTO userInfoDTO = UserInfoDTO.builder()
				.userId(null)
				.mappedExternalUserId(mappedExternalUserId)
				.build();
		UserInfoDTO userInfo = userAuthorizationService.getUserInfo(mappedExternalUserId);
		assertEquals(userInfo, userInfoDTO);
	}

}