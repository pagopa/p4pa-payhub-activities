package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
