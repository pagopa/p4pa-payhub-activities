package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionFlowRetrieverServiceTest {

	@Mock
	private IngestionFlowFileDao ingestionFlowFileDaoMock;

	private IngestionFlowRetrieverService service;

	@BeforeEach
	void init() {
		service = new IngestionFlowRetrieverService(ingestionFlowFileDaoMock);
	}

	@Test
	void givenIngestionFlowIdThenSuccess() {
		Long ingestionFlowId = 1L;
		IngestionFlowFileDTO expected = IngestionFlowFileDTO.builder()
			.ingestionFlowId(ingestionFlowId)
			.build();

		when(ingestionFlowFileDaoMock.getIngestionFlow(ingestionFlowId)).thenReturn(Optional.of(expected));

		IngestionFlowFileDTO actual = service.getIngestionFlow(ingestionFlowId);

		assertEquals(expected, actual);
	}

	@Test
	void givenIngestionFlowIdThenThrowIngestionFlowNotFoundException() {
		Long ingestionFlowId = 1L;
		when(ingestionFlowFileDaoMock.getIngestionFlow(ingestionFlowId)).thenReturn(Optional.empty());
		assertThrows(IngestionFlowNotFoundException.class, () -> service.getIngestionFlow(ingestionFlowId));
	}
}