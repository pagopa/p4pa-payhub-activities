package it.gov.pagopa.payhub.activities.service.email.cache;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailTemplateCacheServiceImplTest {

    public static final String TEMPLATE_FOLDER_BASE_URL = "/tmp/email-templates";

    private EmailTemplateCacheServiceImpl cacheService;

    @BeforeEach
    void setup() {
        cacheService = new EmailTemplateCacheServiceImpl(
                TEMPLATE_FOLDER_BASE_URL
        );
    }

    @Test
    void saveInCache() {
        //GIVEN
        //WHEN
        cacheService.saveInCache(null, "BORKER_EXTERNAL_ID", EmailTemplateName.INGESTION_RECEIPT_OK);
        //THEN
    }

    @Test
    void getFromCache() {
        //GIVEN
        //WHEN
        cacheService.getFromCache("BORKER_EXTERNAL_ID", EmailTemplateName.INGESTION_RECEIPT_OK);
        //THEN
    }

    @Test
    void isTemplateInCache() {
        //GIVEN
        //WHEN
        cacheService.isTemplateInCache("BORKER_EXTERNAL_ID", EmailTemplateName.INGESTION_RECEIPT_OK);
        //THEN
    }

    //TODO-4599 added remaining test cases
}