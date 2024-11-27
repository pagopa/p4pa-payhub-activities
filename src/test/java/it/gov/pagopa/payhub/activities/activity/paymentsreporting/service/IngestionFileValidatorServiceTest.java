package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Path;

class IngestionFileValidatorServiceTest {

	@Mock
	private Path filePath;
	@Mock
	private Path md5FilePath;
	@Mock
	private Path authFilePath;

	@InjectMocks
	private IngestionFileValidatorService ingestionFileValidatorService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}


}
