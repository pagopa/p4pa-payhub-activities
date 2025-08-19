package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public abstract class TreasuryVersionBaseHandlerService<T> implements TreasuryVersionHandlerService {

    private final TreasuryMapperService<T> mapperService;
    private final TreasuryValidatorService<T> validatorService;
    private final TreasuryErrorsArchiverService treasuryErrorsArchiverService;
    private final TreasuryService treasuryService;

    public static final String ORG_BT_CODE = "0000000000";
    public static final String ORG_ISTAT_CODE = "0000000000";

    protected TreasuryVersionBaseHandlerService(TreasuryMapperService<T> mapperService, TreasuryValidatorService<T> validatorService, TreasuryErrorsArchiverService treasuryErrorsArchiverService, TreasuryService treasuryService) {
        this.mapperService = mapperService;
        this.validatorService = validatorService;
        this.treasuryErrorsArchiverService = treasuryErrorsArchiverService;
        this.treasuryService = treasuryService;
    }

    protected abstract T unmarshall(File file);
    protected abstract String getFileVersion();

    @Override
    public Pair<IngestionFlowFileResult, List<Treasury>> handle(File input, IngestionFlowFile ingestionFlowFileDTO, int inputFileNumber) {
        T unmarshalled;
        try {
            unmarshalled = unmarshall(input);
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa with name {} parsing error using version handler {}: {}", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName(), e.getMessage());
            return Pair.of(new IngestionFlowFileResult(), null);
        }

        List<TreasuryErrorDTO> errorDTOList = validate(ingestionFlowFileDTO, inputFileNumber, unmarshalled);
        int notValidTreasuries = errorDTOList.size();

        Map<TreasuryOperationEnum, List<Treasury>> result = mapperService.apply(unmarshalled, ingestionFlowFileDTO);

        log.debug("file flussoGiornaleDiCassa with name {} parsed successfully using mapper {} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
        List<Treasury> deleteTreasuries = handleDeletes(ingestionFlowFileDTO, result, errorDTOList);

        treasuryErrorsArchiverService.writeErrors(input.toPath().getParent(), ingestionFlowFileDTO, errorDTOList);

        List<Treasury> newTreasuries = Objects.requireNonNullElse(result.get(TreasuryOperationEnum.INSERT), List.of());
        int processedRows =
                Optional.ofNullable(deleteTreasuries).map(List::size).orElse(0) +
                        newTreasuries.size();
        return Pair.of(
                IngestionFlowFileResult.builder()
                        .fileVersion(getFileVersion())
                        .totalRows(result.values().stream().mapToLong(List::size).sum() + notValidTreasuries)
                        .processedRows(processedRows)
                        .build(),
                newTreasuries);
    }

    private List<Treasury> handleDeletes(IngestionFlowFile ingestionFlowFileDTO, Map<TreasuryOperationEnum, List<Treasury>> result, List<TreasuryErrorDTO> errorDTOList) {
        List<Treasury> deleteTreasuries = result.get(TreasuryOperationEnum.DELETE);
        if (deleteTreasuries != null) {
            for (Treasury treasuryDTO : deleteTreasuries) {
                Long rowDeleted = treasuryService.deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(
                        treasuryDTO.getOrganizationId(),
                        treasuryDTO.getBillCode(),
                        treasuryDTO.getBillYear(),
                        treasuryDTO.getOrgBtCode(),
                        treasuryDTO.getOrgIstatCode());
                if (rowDeleted == 0L) {
                    errorDTOList.add(TreasuryErrorDTO.builder()
                            .errorMessage("The bill is not present in database so it is impossible to delete it")
                            .errorCode(treasuryDTO.getOrganizationId() + "-" + treasuryDTO.getBillCode() + "-" + treasuryDTO.getBillYear() + "-" + treasuryDTO.getOrgBtCode() + "-" + treasuryDTO.getOrgIstatCode())
                            .billCode(treasuryDTO.getBillCode())
                            .billYear(treasuryDTO.getBillYear())
                            .fileName(ingestionFlowFileDTO.getFileName())
                            .build());
                }
            }
        }
        return deleteTreasuries;
    }

    List<TreasuryErrorDTO> validate(IngestionFlowFile ingestionFlowFileDTO, int size, T fGCUnmarshalled) {
        if (!validatorService.validatePageSize(fGCUnmarshalled, size)) {
            throw new TreasuryOpiInvalidFileException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFileDTO.getFileName());
        }
        return validatorService.validateData(fGCUnmarshalled, ingestionFlowFileDTO.getFileName());
    }


}
