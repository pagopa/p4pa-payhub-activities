package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi171;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi171.FlussoGiornaleDiCassa;
import java.io.File;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(0)
public class TreasuryVersionOpi171HandlerService extends TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> {


    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryVersionOpi171HandlerService(TreasuryMapperOpi171Service mapperService,
                                               TreasuryValidatorOpi171Service validatorService,
                                               TreasuryUnmarshallerService treasuryUnmarshallerService,
                                               TreasuryErrorsArchiverService treasuryErrorsArchiverService,
                                               TreasuryService treasuryService) {
        super(mapperService, validatorService, treasuryErrorsArchiverService, treasuryService);
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }

    @Override
    protected FlussoGiornaleDiCassa unmarshall(File file) {
        return treasuryUnmarshallerService.unmarshalOpi171(file);
    }
}
