package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.InstallmentDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.PersonDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.TransferDTOMapper;
import it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildPaymentsInstallmentDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPaymentsPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.PersonFaker.buildPersonDTO;
import static it.gov.pagopa.payhub.activities.util.faker.TransferFaker.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InstallmentDTOMapperTest {

    @Mock
    private TransferDTOMapper transferDTOMapperMock;

    @Mock
    private PersonDTOMapper personDTOMapperMock;

    @InjectMocks
    private final InstallmentDTOMapper mapper = Mappers.getMapper(InstallmentDTOMapper.class);

    @Test
    void testMapInstallmentDTO(){
        InstallmentDTO installmentDTOexpected = buildPaymentsInstallmentDTO();
        installmentDTOexpected.setDebtPositionOrigin(null);

        Mockito.when(transferDTOMapperMock.map(buildTransferDTO())).thenReturn(buildPaymentsTransferDTO());
        Mockito.when(personDTOMapperMock.map(buildPersonDTO())).thenReturn(buildPaymentsPersonDTO());

        InstallmentDTO result = mapper.map(buildInstallmentDTO());

        assertEquals(installmentDTOexpected, result);
        checkNotNullFields(result, "debtPositionOrigin", "syncStatus");
    }
}
