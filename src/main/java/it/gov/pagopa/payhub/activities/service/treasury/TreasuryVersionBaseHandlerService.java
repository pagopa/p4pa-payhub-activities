package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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
    private final TreasuryErrorsArchiverService treasuryErrorsArchiverService;
    private final TreasuryService treasuryService;


    protected TreasuryVersionBaseHandlerService(TreasuryMapperService<T> mapperService, TreasuryValidatorService<T> validatorService, TreasuryErrorsArchiverService treasuryErrorsArchiverService, TreasuryService treasuryService) {
        this.mapperService = mapperService;
        this.validatorService = validatorService;
        this.treasuryErrorsArchiverService = treasuryErrorsArchiverService;
        this.treasuryService = treasuryService;
    }

    protected abstract T unmarshall(File file);

    @Override
    public List<Treasury> handle(File input, IngestionFlowFile ingestionFlowFileDTO, int size) {
        try {
            T unmarshalled = unmarshall(input);
            List<TreasuryErrorDTO> errorDTOList = validate(ingestionFlowFileDTO, size, unmarshalled);
            Map<TreasuryOperationEnum, List<Treasury>> result = mapperService.apply(unmarshalled, ingestionFlowFileDTO);
            log.debug("file flussoGiornaleDiCassa with name {} parsed successfully using mapper {} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            List<Treasury> deleteTreasuries = result.get(TreasuryOperationEnum.DELETE);
            for (Treasury treasuryDTO : deleteTreasuries) {
                Long rowDeleted = treasuryService.deleteByOrganizationIdAndBillCodeAndBillYear(
                                treasuryDTO.getOrganizationId(),
                                treasuryDTO.getBillCode(),
                                treasuryDTO.getBillYear());
                if (rowDeleted == 0L) {
                    errorDTOList.add(TreasuryErrorDTO.builder()
                            .errorMessage("The bill is not present in database so it is impossible to delete it")
                            .errorCode(treasuryDTO.getOrganizationId()+"-"+treasuryDTO.getBillCode()+"-"+treasuryDTO.getBillYear())
                            .billCode(treasuryDTO.getBillCode())
                            .billYear(treasuryDTO.getBillYear())
                            .fileName(ingestionFlowFileDTO.getFileName())
                            .build());
                }
            }

            treasuryErrorsArchiverService.writeErrors(input.toPath().getParent(), ingestionFlowFileDTO, errorDTOList);
            return result.get(TreasuryOperationEnum.INSERT);
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa with name {} parsing error using mapper{} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            return Collections.emptyList();
        }
    }

    List<TreasuryErrorDTO> validate(IngestionFlowFile ingestionFlowFileDTO, int size, T fGCUnmarshalled) {
        if (!validatorService.validatePageSize(fGCUnmarshalled, size)) {
            throw new TreasuryOpiInvalidFileException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFileDTO.getFileName());
        }
        return validatorService.validateData(fGCUnmarshalled, ingestionFlowFileDTO.getFileName());
    }



}
