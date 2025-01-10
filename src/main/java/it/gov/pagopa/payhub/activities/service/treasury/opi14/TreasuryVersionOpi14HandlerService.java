package it.gov.pagopa.payhub.activities.service.treasury.opi14;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Order(1)
public class TreasuryVersionOpi14HandlerService extends TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> {


    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryVersionOpi14HandlerService(TreasuryMapperOpi14Service mapperService,
                                              TreasuryValidatorOpi14Service validatorService,
                                              TreasuryUnmarshallerService treasuryUnmarshallerService,
                                              TreasuryErrorsArchiverService treasuryErrorsArchiverService) {
        super(mapperService, validatorService, treasuryErrorsArchiverService);
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }

    @Override
    public FlussoGiornaleDiCassa unmarshall(File file) {
        return treasuryUnmarshallerService.unmarshalOpi14(file);
    }
}
