package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.opi14;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@Order(1)
public class TreasuryVersionOpi14HandlerService extends TreasuryVersionBaseHandlerService<FlussoGiornaleDiCassa> {


    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryVersionOpi14HandlerService(TreasuryMapperOpi14Service mapperService,
                                              TreasuryValidatorOpi14Service validatorService,
                                              TreasuryUnmarshallerService treasuryUnmarshallerService,
                                              TreasuryErrorsArchiverService treasuryErrorsArchiverService,
                                              TreasuryService treasuryService) {
        super(mapperService, validatorService, treasuryErrorsArchiverService, treasuryService);
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }

    @Override
    public FlussoGiornaleDiCassa unmarshall(File file) {
        log.info("Unmarshalling OPI14 file [{}]", file.getName());
        return treasuryUnmarshallerService.unmarshalOpi14(file);
    }
}
