package it.gov.pagopa.payhub.activities.service.receipt;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.service.files.XMLMarshallerService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtReceiptV2;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Lazy
@Service
@Slf4j
public class RtFileHandlerService {

    private final String dataCipherPsw;

    private final XMLMarshallerService xmlMarshallerService;
    private final JAXBContext jaxbContext;
    private final FoldersPathsConfig foldersPathsConfig;

    public RtFileHandlerService(
            @Value("${cipher.file-encrypt-psw}") String dataCipherPsw,

            XMLMarshallerService xmlMarshallerService,
            FoldersPathsConfig foldersPathsConfig) throws JAXBException {
        this.dataCipherPsw = dataCipherPsw;
        this.xmlMarshallerService = xmlMarshallerService;
        this.foldersPathsConfig = foldersPathsConfig;

        this.jaxbContext = JAXBContext.newInstance(CtReceiptV2.class);
    }

    public String store(Long organizationId, CtReceiptV2 rt, String fileName) {
        return store(organizationId, xmlMarshallerService.marshal(CtReceiptV2.class, rt, jaxbContext), fileName);
    }

    public String store(Long organizationId, String rt, String fileName) {
        byte[] encrypted = AESUtils.encrypt(dataCipherPsw, rt);
        Path filePath = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), organizationId)
                .resolve(foldersPathsConfig.getPaths().getRtFolder());
        try {
            Files.createDirectories(filePath);
            Files.write(
                    filePath
                            .resolve(fileName + AESUtils.CIPHER_EXTENSION),
                    encrypted);
            return foldersPathsConfig.getPaths().getRtFolder() + File.separator + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot store RT having name " + fileName + " retrieved for organizationId " + organizationId, e);
        }
    }

    public String read(Long organizationId, ReceiptDTO receipt) {
        return read(organizationId, receipt.getRtFilePath());
    }

    public String read(Long organizationId, String rtFilePath) {
        Path organizationBasePath = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), organizationId);
        Path rtFile = organizationBasePath.resolve(rtFilePath + AESUtils.CIPHER_EXTENSION);
        if (Files.exists(rtFile)) {
            try {
                return AESUtils.decrypt(dataCipherPsw, Files.readAllBytes(rtFile));
            } catch (IOException e) {
                log.error("Cannot read RT file having path {} and related to organizationId {}", rtFilePath, organizationId, e);
                return null;
            }
        } else {
            return null;
        }
    }
}
