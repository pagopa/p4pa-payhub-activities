package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceTest {
	@Mock
	private UserAuthorizationServiceImpl userAuthorizationService;
	@Mock
	UserInfoDTO userInfoDTO;

	UserInfoDTO userInfo;

	@BeforeEach
	void setup() {
		userAuthorizationService = new UserAuthorizationServiceImpl();
		userInfoDTO = new UserInfoDTO();
	}

	@Test
	void testUser() {
		String mappedExternalUserId = "USER";

		userInfoDTO = UserInfoDTO.builder()
				.userId(null)
				.email("testmail@mail.com")
				.mappedExternalUserId(mappedExternalUserId)
				.build();
		userInfo = userAuthorizationService.getUserInfo(mappedExternalUserId);
		assertEquals(userInfo, userInfoDTO);
	}

}