package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
