package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class IngestionFileHandlerServiceTest {

	private static final String TEST_PATH = "some/path";
	private static final String TEST_FILENAME = "testFile.zip";
	private static final String TEST_CIPHER_PSW = "testPassword";

	private IngestionFileHandlerService ingestionFileHandlerService;


	@BeforeEach
	void setUp() {
		ingestionFileHandlerService = new IngestionFileHandlerService(TEST_CIPHER_PSW);
	}

	@Test
	void setUpProcessSuccess() throws IOException {

	}

	@Test
	void decryptFailed() throws IOException {

	}

	@Test
	void validateZipFailed() throws IOException {

	}

	@Test
	void unzipFileFailed() throws IOException {

	}
}
