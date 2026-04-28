package it.gov.pagopa.payhub.activities.service.email.remote.cache;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RemoteEmailTemplateCacheServiceImplTest {

    private static final String TEMPLATE_FOLDER_BASE_PATH = "/tmp/email-template";

    private static final String BROKER_EXTERNAL_ID = "BROKER_EXTERNAL_ID";
    private static final EmailTemplateName TEMPLATE_NAME = EmailTemplateName.INGESTION_RECEIPT_OK;
    private static final EmailTemplateName CACHED_TEMPLATE_NAME = EmailTemplateName.INGESTION_RECEIPT_KO;

    private static EmailTemplate expectedCachedEmailTemplate;

    private RemoteEmailTemplateCacheServiceImpl cacheService;

    @BeforeAll
    static void setupCache() throws IOException {
        expectedCachedEmailTemplate = new EmailTemplate();
        expectedCachedEmailTemplate.setSubject("cached template");
        Path templateFolderPath = Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, CACHED_TEMPLATE_NAME.name());
        Files.createDirectories(templateFolderPath);
        File templateFile = templateFolderPath.resolve(CACHED_TEMPLATE_NAME.name()).toFile();
        try (FileOutputStream fos = new FileOutputStream(templateFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(expectedCachedEmailTemplate);
        } catch (IllegalStateException | IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @BeforeEach
    void setupCacheService() {
        cacheService = new RemoteEmailTemplateCacheServiceImpl(
                TEMPLATE_FOLDER_BASE_PATH
        );
    }

    @AfterEach
    void tearDownFolder() {
        try {
            FileUtils.deleteDirectory(Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, TEMPLATE_NAME.name()).toFile());
        } catch (IOException e) {
            log.warn("Error in deleting tests files", e);
        }
    }

    @AfterAll
    static void tearDownCache() {
        try {
            FileUtils.deleteDirectory(Path.of(TEMPLATE_FOLDER_BASE_PATH).toFile());
        } catch (IOException e) {
            log.warn("Error in deleting tests files", e);
        }
    }

    @Test
    void givenNotExistingDirectoryWhenSaveInCacheThenCreateDirectoryAndSaveTemplate() {
        //GIVEN
        //WHEN
        EmailTemplate expectedTemplate = new EmailTemplate();
        expectedTemplate.setBody("file content");
        cacheService.saveInCache(expectedTemplate, BROKER_EXTERNAL_ID, TEMPLATE_NAME);
        //THEN
        try (FileInputStream fis = new FileInputStream(Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, TEMPLATE_NAME.name(), TEMPLATE_NAME.name()).toFile());
             ObjectInputStream oos = new ObjectInputStream(fis)) {
            EmailTemplate actualTemplate = (EmailTemplate) oos.readObject();
            Assertions.assertEquals(expectedTemplate, actualTemplate);
        } catch (IOException | ClassNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void givenExistingDirectoryWhenSaveInCacheThenSaveTemplate() throws IOException {
        //GIVEN
        Files.createDirectories(Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, TEMPLATE_NAME.name()));
        //WHEN
        EmailTemplate expectedTemplate = new EmailTemplate();
        expectedTemplate.setBody("file content");
        cacheService.saveInCache(expectedTemplate, BROKER_EXTERNAL_ID, TEMPLATE_NAME);
        //THEN
        try (FileInputStream fis = new FileInputStream(Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, TEMPLATE_NAME.name(), TEMPLATE_NAME.name()).toFile());
             ObjectInputStream oos = new ObjectInputStream(fis)) {
            EmailTemplate actualTemplate = (EmailTemplate) oos.readObject();
            Assertions.assertEquals(expectedTemplate, actualTemplate);
        } catch (IOException | ClassNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void givenErrorInCreatingNotExistingDirectoryWhenSaveInCacheThenDoesNotSaveTemplate() {
        //GIVEN
        //WHEN
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path templateFilePath = Path.of(TEMPLATE_FOLDER_BASE_PATH, BROKER_EXTERNAL_ID, TEMPLATE_NAME.name());
            mockedFiles.when(() -> Files.createDirectories(templateFilePath))
                    .thenThrow(new IOException());
            EmailTemplate expectedTemplate = new EmailTemplate();
            cacheService.saveInCache(expectedTemplate, BROKER_EXTERNAL_ID, TEMPLATE_NAME);
            //THEN
            File templateFile = templateFilePath.toFile();
            Assertions.assertFalse(templateFile.exists());
        }
    }

    @Test
    void givenExistingFileWhenGetFromCacheThenReturnTemplate() {
        //GIVEN
        //WHEN
        EmailTemplate actualCacheTemplate = cacheService.getFromCache(BROKER_EXTERNAL_ID, CACHED_TEMPLATE_NAME);
        //THEN
        Assertions.assertEquals(expectedCachedEmailTemplate, actualCacheTemplate);
    }

    @Test
    void givenNonExistingFileWhenGetFromCacheThenReturnNull() {
        //GIVEN
        //WHEN
        EmailTemplate actualCacheTemplate = cacheService.getFromCache(BROKER_EXTERNAL_ID, TEMPLATE_NAME);
        //THEN
        Assertions.assertNull(actualCacheTemplate);
    }

}