package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi161;

import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerServiceTest;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionOpi161HandlerServiceTest extends TreasuryVersionBaseHandlerServiceTest<FlussoGiornaleDiCassa> {

    @Mock
    private TreasuryMapperOpi161Service mapperServiceMock;
    @Mock
    private TreasuryValidatorOpi161Service validatorServiceMock;

    @Override
    protected FlussoGiornaleDiCassa mockFlussoGiornaleDiCassa() {
        return mock(FlussoGiornaleDiCassa.class);
    }

    @Override
    protected TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> buildVersionHandlerService() {
        return new TreasuryVersionOpi161HandlerService(mapperServiceMock, validatorServiceMock, treasuryUnmarshallerServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock, fileExceptionHandlerServiceMock);
    }

    @Override
    protected String getExpectedFileVersion() {
        return "1.6.1";
    }

    @Override
    protected OngoingStubbing<FlussoGiornaleDiCassa> getUnmarshallerMockitOngoingStubbing(File xmlFile) {
        return Mockito.when(treasuryUnmarshallerServiceMock.unmarshalOpi161(xmlFile));
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
