package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi14;

import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerServiceTest;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionOpi14HandlerServiceTest extends TreasuryVersionBaseHandlerServiceTest<FlussoGiornaleDiCassa> {

    @Mock
    private TreasuryMapperOpi14Service mapperServiceMock;
    @Mock
    private TreasuryValidatorOpi14Service validatorServiceMock;

    @Override
    protected FlussoGiornaleDiCassa mockFlussoGiornaleDiCassa() {
        return mock(FlussoGiornaleDiCassa.class);
    }

    @Override
    protected TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> buildVersionHandlerService() {
        return new TreasuryVersionOpi14HandlerService(mapperServiceMock, validatorServiceMock, treasuryUnmarshallerServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock);
    }

    @Override
    protected OngoingStubbing<FlussoGiornaleDiCassa> getUnmarshallerMockitOngoingStubbing(File xmlFile) {
        return Mockito.when(treasuryUnmarshallerServiceMock.unmarshalOpi14(xmlFile));
    }

    @Override
    protected TreasuryValidatorService<FlussoGiornaleDiCassa> getValidatorServiceMock() {
        return validatorServiceMock;
    }

    @Override
    protected TreasuryMapperService<FlussoGiornaleDiCassa> getMapperServiceMock() {
        return mapperServiceMock;
    }
}
