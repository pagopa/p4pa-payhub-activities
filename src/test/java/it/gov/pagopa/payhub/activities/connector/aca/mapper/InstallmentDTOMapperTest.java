package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.InstallmentDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.PersonDTOMapper;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.TransferDTOMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        // Given
        InstallmentDTO installmentDTO = buildInstallmentDTO();

        TransferDTO expectedPaymentsTransfer = Mockito.mock(TransferDTO.class);
        Mockito.when(transferDTOMapperMock.map(installmentDTO.getTransfers().getFirst()))
                .thenReturn(expectedPaymentsTransfer);

        PersonDTO expectedPaymentsPerson = Mockito.mock(PersonDTO.class);
        Mockito.when(personDTOMapperMock.map(installmentDTO.getDebtor()))
                .thenReturn(expectedPaymentsPerson);

        // When
        it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO result = mapper.map(installmentDTO);

        // Then
        TestUtils.reflectionEqualsByName(installmentDTO, result, "transfers", "debtor");
        assertSame(expectedPaymentsTransfer, result.getTransfers().getFirst());
        assertSame(expectedPaymentsPerson, result.getDebtor());
        checkNotNullFields(result, "debtPositionOrigin");
    }
}
