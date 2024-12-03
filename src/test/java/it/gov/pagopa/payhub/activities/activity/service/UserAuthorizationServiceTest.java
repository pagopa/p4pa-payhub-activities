package it.gov.pagopa.payhub.activities.activity.service;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.UserDTO;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.service.auth.UserAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceTest {
	@Mock
	private UserAuthorizationService service;

	@BeforeEach
	void init() {
	}

	@Test
	void testUser() {
		String mappedExternalUserId = "TEST_USER";
		UserInfoDTO userInfoDTO = service.getUserInfoDTO(mappedExternalUserId);
		assertEquals(mappedExternalUserId, userInfoDTO.getMappedExternalUserId());
	}
}