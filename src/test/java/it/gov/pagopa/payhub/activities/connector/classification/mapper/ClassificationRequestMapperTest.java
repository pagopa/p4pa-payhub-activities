package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.ClassificationFaker;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationRequestBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClassificationRequestMapperTest {

    @Test
    void testMap() {
        // Given
        Classification classification = ClassificationFaker.buildFullClassificationDTO();


        // When
        ClassificationRequestBody result = ClassificationRequestMapper.map(classification);

        // Then
        assertEquals(classification.getClassificationId(), result.getClassificationId());
        assertEquals(classification.getLabel(), result.getLabel());
        assertEquals(classification.getOrganizationId(), result.getOrganizationId());
        assertEquals(classification.getIuf(), result.getIuf());
        assertEquals(classification.getIuv(), result.getIuv());
        assertEquals(classification.getCreationDate(), result.getCreationDate());
        assertEquals(classification.getUpdateDate(), result.getUpdateDate());

        TestUtils.checkNotNullFields(result, "updateOperatorExternalId", "updateDate", "creationDate");
    }

    @Test
    void testMap_NullClassification() {
        // Given
        Classification classification = null;

        // When
        ClassificationRequestBody result = ClassificationRequestMapper.map(classification);

        // Then
        assertNull(result);
    }
}