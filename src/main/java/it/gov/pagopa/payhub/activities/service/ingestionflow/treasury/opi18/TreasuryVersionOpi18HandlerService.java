package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi18;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi18.FlussoGiornaleDiCassa;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Order(0)
public class TreasuryVersionOpi18HandlerService extends TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> {

    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryVersionOpi18HandlerService(TreasuryMapperOpi18Service mapperService,
                                               TreasuryValidatorOpi18Service validatorService,
                                               TreasuryUnmarshallerService treasuryUnmarshallerService,
                                               TreasuryErrorsArchiverService treasuryErrorsArchiverService,
                                               TreasuryService treasuryService,
                                               FileExceptionHandlerService fileExceptionHandlerService) {
        super(mapperService, validatorService, treasuryErrorsArchiverService, treasuryService, fileExceptionHandlerService);
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }

    @Override
    protected FlussoGiornaleDiCassa unmarshall(File file) {
        return treasuryUnmarshallerService.unmarshalOpi18(file);
    }

    @Override
    protected String getFileVersion() {
        return "1.8";
    }
}
