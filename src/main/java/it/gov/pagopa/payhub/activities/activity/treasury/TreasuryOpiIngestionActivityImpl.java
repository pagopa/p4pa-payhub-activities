package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.IufIuvDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResultDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryUnmarshallerService treasuryUnmarshallerService;


    public TreasuryOpiIngestionActivityImpl(@Value("${ingestion-flow-file-type:O}") String ingestionflowFileType,
                                            IngestionFlowFileDao ingestionFlowFileDao,
                                            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
                                            TreasuryUnmarshallerService treasuryUnmarshallerService) {
        this.ingestionflowFileType = ingestionflowFileType;
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
    }


    @Override
    public TreasuryIngestionResultDTO processFile(Long ingestionFlowFileId) {
        List<IufIuvDTO> iufIuvList = new ArrayList<>();
        List<Path> ingestionFlowFiles = null;
        AtomicBoolean success = new AtomicBoolean(true);
        try {
            IngestionFlowFileDTO ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);

        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIngestionResultDTO(Collections.emptyList(), false);
        }

        if (ingestionFlowFiles != null && !ingestionFlowFiles.isEmpty()) {
            ingestionFlowFiles.forEach(path -> {
                File ingestionFlowFile = path.toFile();
                log.debug("file from zip archive with name {} loaded successfully ", ingestionFlowFile.getName());

                success.set(parseData(ingestionFlowFile));

            });
        }
        return new TreasuryIngestionResultDTO(iufIuvList, success.get());
    }

    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
            throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
    }

    private boolean parseData(File ingestionFlowFile) {


        it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa flussoGiornaleDiCassa14 = null;
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa161 = null;

        try {
            flussoGiornaleDiCassa161 = treasuryUnmarshallerService.unmarshalOpi161(ingestionFlowFile);
            log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa161.getId());
        } catch (Exception e) {
            log.error("file flussoGiornaleDiCassa parsing error with opi 1.6.1 format {} ", e.getMessage());
        }
        if (flussoGiornaleDiCassa161 == null) {
            try {
                flussoGiornaleDiCassa14 = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
                log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa14.getId());
            } catch (Exception e) {
                log.error("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", e.getMessage());
                return false;
            }
        }
        return true;
    }

}
