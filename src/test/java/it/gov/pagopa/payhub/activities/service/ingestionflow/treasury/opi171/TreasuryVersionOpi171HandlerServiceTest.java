package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi171;

import static org.mockito.Mockito.mock;

import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerServiceTest;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi171.FlussoGiornaleDiCassa;
import java.io.File;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionOpi171HandlerServiceTest extends TreasuryVersionBaseHandlerServiceTest<FlussoGiornaleDiCassa> {

    @Mock
    private TreasuryMapperOpi171Service mapperServiceMock;
    @Mock
    private TreasuryValidatorOpi171Service validatorServiceMock;
    @Mock
    private FileExceptionHandlerService fileExceptionHandlerServiceMock;

    @Override
    protected FlussoGiornaleDiCassa mockFlussoGiornaleDiCassa() {
        return mock(FlussoGiornaleDiCassa.class);
    }

    @Override
    protected TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> buildVersionHandlerService() {
        return new TreasuryVersionOpi171HandlerService(mapperServiceMock, validatorServiceMock, treasuryUnmarshallerServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock, fileExceptionHandlerServiceMock);
    }

    @Override
    protected String getExpectedFileVersion() {
        return "1.7.1";
    }

    @Override
    protected OngoingStubbing<FlussoGiornaleDiCassa> getUnmarshallerMockitOngoingStubbing(File xmlFile) {
        return Mockito.when(treasuryUnmarshallerServiceMock.unmarshalOpi171(xmlFile));
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
