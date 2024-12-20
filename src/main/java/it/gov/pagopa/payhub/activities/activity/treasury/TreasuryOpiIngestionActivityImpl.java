package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryUnmarshallerService treasuryUnmarshallerService;


    public TreasuryOpiIngestionActivityImpl(
                                            IngestionFlowFileDao ingestionFlowFileDao,
                                            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                            TreasuryUnmarshallerService treasuryUnmarshallerService) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {

        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            List<Path> ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);


           List <String> iufList = ingestionFlowFiles.stream()
                    .map(this::parseData)
                    .flatMap(List::stream)
                    .toList();


            return new TreasuryIufResult(iufList,true);

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIufResult(Collections.emptyList(), false);
        }


    }

    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(IngestionFlowFileType.OPI)) {
            throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePath()), ingestionFlowFileDTO.getFileName());
    }

    private List<String> parseData(Path ingestionFlowFilePath) {
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
