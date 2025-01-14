package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildPaymentsTransferDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.buildTransferDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TransferDTOMapperTest {

    @InjectMocks
    private final TransferDTOMapper mapper = Mappers.getMapper(TransferDTOMapper.class);

    @Test
    void testMapTransferDTO(){
        TransferDTO transferDTOexpected = buildPaymentsTransferDTO();

        TransferDTO result = mapper.map(buildTransferDTO());

        assertEquals(transferDTOexpected, result);
        checkNotNullFields(result);
    }
}
