package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuv;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TreasuryMapperTest {

    @Mock
    private TreasuryMapperInner mapperInnerMock;

    private TreasuryMapper mapper;

    @BeforeEach
    void init(){
        this.mapper = new TreasuryMapper(mapperInnerMock);
    }

//region test map2Iuf
    @Test
    void givenTreasuryIufWhenMap2IufThenOk(){
        // Given
        Treasury treasury = new Treasury();
        treasury.setIuf("IUF");
        TreasuryIuf expectedResult = new TreasuryIuf();

        Mockito.when(mapperInnerMock.map2Iuf(Mockito.same(treasury)))
                 .thenReturn(expectedResult);

        // When
        TreasuryIuf result = mapper.map2Iuf(treasury);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotTreasuryIufWhenMap2IufThenThrowIllegalArgumentException(){
        // Given
        Treasury treasury = new Treasury();

        // When, Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.map2Iuf(treasury));
    }

    @Test
    void givenNullWhenMap2IufThenNull(){
        // When, Then
        Assertions.assertNull(mapper.map2Iuf(null));
    }
//endregion

//region test map2Iuv
    @Test
    void givenTreasuryIuvWhenMap2IuvThenOk(){
        // Given
        Treasury treasury = new Treasury();
        treasury.setIuv("IUV");
        TreasuryIuv expectedResult = new TreasuryIuv();

        Mockito.when(mapperInnerMock.map2Iuv(Mockito.same(treasury)))
                .thenReturn(expectedResult);

        // When
        TreasuryIuv result = mapper.map2Iuv(treasury);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotTreasuryIuvWhenMap2IuvThenThrowIllegalArgumentException(){
        // Given
        Treasury treasury = new Treasury();

        // When, Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.map2Iuv(treasury));
    }

    @Test
    void givenNullWhenMap2IuvThenNull(){
        // When, Then
        Assertions.assertNull(mapper.map2Iuv(null));
    }
//endregion
}
