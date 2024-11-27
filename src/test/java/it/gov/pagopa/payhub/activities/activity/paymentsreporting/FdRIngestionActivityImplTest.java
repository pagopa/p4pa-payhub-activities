package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileValidatorService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FdRIngestionActivityImplTest {

	@Mock
	private IngestionFlowRetrieverService ingestionFlowRetrieverServiceMock;
	@Mock
	private IngestionFileValidatorService ingestionFileValidatorServiceMock;
	@Mock
	private IngestionFileHandlerService ingestionFileHandlerServiceMock;

	private FdRIngestionActivityImpl activity;

	@BeforeEach
	void init() {
		activity = new FdRIngestionActivityImpl(ingestionFlowRetrieverServiceMock, ingestionFileValidatorServiceMock, ingestionFileHandlerServiceMock);
	}


}