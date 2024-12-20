package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Lazy
@Service
@Slf4j
public class TreasuryOpiParserService {

    private final TreasuryUnmarshallerService treasuryUnmarshallerService;

    public TreasuryOpiParserService(TreasuryUnmarshallerService treasuryUnmarshallerService) {
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }


    public List<String> parseData(Path ingestionFlowFilePath) {
        File ingestionFlowFile=ingestionFlowFilePath.toFile();

        it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa flussoGiornaleDiCassa14 = null;
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa161 = null;

        try {
            flussoGiornaleDiCassa161 = treasuryUnmarshallerService.unmarshalOpi161(ingestionFlowFile);
            log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa161.getId());
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa parsing error with opi 1.6.1 format {} ", e.getMessage());
            try {
                flussoGiornaleDiCassa14 = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
                log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa14.getId());
            } catch (Exception exception) {
                log.info("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", exception.getMessage());
                throw new TreasuryOpiInvalidFileException("Cannot parse treasury Opi file " + ingestionFlowFile);
            }
        }
        //TODO in task 1658 it will be implemented the element to be returned
        return List.of();
    }

}
