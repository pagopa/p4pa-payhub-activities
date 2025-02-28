package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TreasuryMapperInnerTest {

    private final TreasuryMapperInner mapper = Mappers.getMapper(TreasuryMapperInner.class);

    @Test
    void map2Iuf() {
        // Given
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();

        // When
        TreasuryIuf result = mapper.map2Iuf(treasury);

        // Then
        TestUtils.reflectionEqualsByName(treasury, result);
        TestUtils.checkNotNullFields(result);
    }

    @Test
    void map2Iuv() {
        // Given
        Treasury treasury = TreasuryFaker.buildTreasuryDTO();

        // When
        TreasuryIuv result = mapper.map2Iuv(treasury);

        // Then
        TestUtils.reflectionEqualsByName(treasury, result);
        TestUtils.checkNotNullFields(result);
    }
}