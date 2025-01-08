package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public abstract class TreasuryVersionBaseHandlerService <T> implements TreasuryVersionHandlerService{
    
    private final TreasuryMapperService<T> mapperService;
    private final TreasuryValidatorService<T> validatorService;
    

    protected TreasuryVersionBaseHandlerService(TreasuryMapperService<T> mapperService, TreasuryValidatorService<T> validatorService) {
        this.mapperService = mapperService;
        this.validatorService = validatorService;
    }

    abstract T unmarshall(File file);

    @Override
    public Map<TreasuryOperationEnum, List<TreasuryDTO>> handle(File input, IngestionFlowFileDTO ingestionFlowFileDTO, int size) {
        try {
            T unmarshalled = unmarshall(input);
            validate(ingestionFlowFileDTO, size, unmarshalled);
            Map<TreasuryOperationEnum, List<TreasuryDTO>> result = mapperService.apply(unmarshalled, ingestionFlowFileDTO);
            log.debug("file flussoGiornaleDiCassa with name {} parsed successfully using mapper {} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            return result;
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa with name {} parsing error using mapper{} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            return Collections.emptyMap();
        }
    }

    private void validate(IngestionFlowFileDTO ingestionFlowFileDTO, int size, T fGCUnmarshalled) {
        if (validatorService.validatePageSize(fGCUnmarshalled, size)) {
            throw new TreasuryOpiInvalidFileException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFileDTO.getFileName());
        }
        validatorService.validateData(fGCUnmarshalled, ingestionFlowFileDTO.getFileName());
    }
}
