package it.gov.pagopa.payhub.activities.service.treasury.opi161;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Order(0)
public class TreasuryVersionOpi161HandlerService extends TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> {


    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryVersionOpi161HandlerService(TreasuryMapperOpi161Service mapperService,
                                               TreasuryValidatorOpi161Service validatorService,
                                               TreasuryUnmarshallerService treasuryUnmarshallerService,
                                               TreasuryErrorsArchiverService treasuryErrorsArchiverService,
                                               TreasuryService treasuryService) {
        super(mapperService, validatorService, treasuryErrorsArchiverService, treasuryService);
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }

    @Override
    public FlussoGiornaleDiCassa unmarshall(File file) {
        return treasuryUnmarshallerService.unmarshalOpi161(file);
    }
}
