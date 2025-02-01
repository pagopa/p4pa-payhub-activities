package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.ClassificationFaker;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationRequestBody;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

class ClassificationRequestMapperTest {

    private final ClassificationRequestMapper mapper = Mappers.getMapper(ClassificationRequestMapper.class);

    @Test
    void testMap() {
        // Given
        Classification classification = ClassificationFaker.buildClassificationDTO();


        // When
        ClassificationRequestBody result = mapper.map(classification);

        // Then
        TestUtils.reflectionEqualsByName(classification, result);
        TestUtils.checkNotNullFields(result, "updateOperatorExternalId", "updateDate", "creationDate");
    }

    @Test
    void testMap_NullClassification() {
        // Given
        Classification classification = null;

        // When
        ClassificationRequestBody result = mapper.map(classification);

        // Then
        assertNull(result);
    }
}