package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPaymentsPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPersonDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PersonDTOMapperTest {

    @InjectMocks
    private final PersonDTOMapper mapper = Mappers.getMapper(PersonDTOMapper.class);

    @Test
    void testMapPersonDTO(){
        PersonDTO personDTOexpected = buildPaymentsPersonDTO();

        PersonDTO result = mapper.map(buildPersonDTO());

        assertEquals(personDTOexpected, result);
        checkNotNullFields(result);
    }
}
