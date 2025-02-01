package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryRequestBody;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

class TreasuryRequestMapperTest {

    private final TreasuryRequestMapper mapper = Mappers.getMapper(TreasuryRequestMapper.class);

    @Test
    void testMap() {
        // Given
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();

        // When
        TreasuryRequestBody result = mapper.map(treasury);

        // Then
        TestUtils.reflectionEqualsByName(treasury, result);
        TestUtils.checkNotNullFields(result);
    }

    @Test
    void testMap_NullTreasury() {
        // Given
        Treasury treasury = null;

        // When
        TreasuryRequestBody result = mapper.map(treasury);

        // Then
        assertNull(result);
    }
}