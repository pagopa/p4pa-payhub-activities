package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDto;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryMapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {

    private final String ingestionflowFileType;
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final TreasuryDao treasuryDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryUnmarshallerService treasuryUnmarshallerService;
    private final TreasuryMapperService treasuryMapperService;



    public TreasuryOpiIngestionActivityImpl(@Value("${ingestion-flow-file-type:O}") String ingestionflowFileType,
                                            IngestionFlowFileDao ingestionFlowFileDao, TreasuryDao treasuryDao,
                                            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                            TreasuryUnmarshallerService treasuryUnmarshallerService,
                                            TreasuryMapperService treasuryMapperService) {
        this.ingestionflowFileType = ingestionflowFileType;
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.treasuryDao = treasuryDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
        this.treasuryMapperService = treasuryMapperService;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {
        List<String> iufList = new ArrayList<>();
        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                    .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
            if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
                throw new IllegalArgumentException("invalid ingestionFlow file type");
            }

            List<Path> ingestionFlowFiles = ingestionFlowFileRetrieverService
                    .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

            ingestionFlowFiles.forEach(path -> {
                FlussoGiornaleDiCassa flussoGiornaleDiCassa = treasuryUnmarshallerService.unmarshal(path.toFile());
                      //  log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa.getCodIdUnivocoFlusso());

                        //valida campi
                        TreasuryDto treasuryDto = treasuryMapperService.apply(flussoGiornaleDiCassa);

                        treasuryDao.insert(treasuryDto);

                        iufList.add(treasuryDto.getCodIdUnivocoFlusso());
                    }
            );

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {} due to: {}", ingestionFlowFileId, e.getMessage());
            return new TreasuryIufResult(Collections.emptyList(), false);
        }
        return new TreasuryIufResult(iufList, true);
    }


}
