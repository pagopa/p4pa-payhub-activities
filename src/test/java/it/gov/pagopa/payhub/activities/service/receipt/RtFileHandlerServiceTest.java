package it.gov.pagopa.payhub.activities.service.receipt;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.service.files.XMLMarshallerService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtReceiptV2;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.CtSubject;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class RtFileHandlerServiceTest {

    @Mock
    private FoldersPathsConfig foldersPathsConfigMock;

    @TempDir
    private Path tmpDir;

    private RtFileHandlerService service;

    @BeforeEach
    void init() throws JAXBException {
        String dataCipherPsw = "PSW";
        service = new RtFileHandlerService(dataCipherPsw, new XMLMarshallerService(), foldersPathsConfigMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(foldersPathsConfigMock);
    }

    @Test
    void whenStoreThenEncryptAndStore_whenReadThenDecrypt(){
        // Given
        long organizationId = 1L;
        CtReceiptV2 rt = new CtReceiptV2();
        rt.setCompanyName("COMPANY");
        rt.setFiscalCode("FC");
        rt.setPaymentAmount(BigDecimal.TEN);
        CtSubject subject = new CtSubject();
        subject.setFullName("FULLNAME");
        rt.setDebtor(subject);

        String fileName = "rtFileName.xml";

        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(tmpDir);
        Mockito.when(foldersPathsConfigMock.getPaths())
                .thenReturn(FoldersPathsConfig.FoldersPaths.builder()
                        .rtFolder("data/rt/path")
                        .build());

        String expectedRtFilePath = "data/rt/path/rtFileName.xml";
        Path expectedStoredPath = tmpDir.resolve("1").resolve(expectedRtFilePath + AESUtils.CIPHER_EXTENSION);

        // When store
        String resultStore = service.store(organizationId, rt, fileName);

        // Then
        Assertions.assertEquals(expectedRtFilePath, resultStore);
        Assertions.assertTrue(Files.exists(expectedStoredPath), "Cannot find rtFile on the file system: " + expectedStoredPath);

        // When read
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setRtFilePath(expectedRtFilePath);

        String resultRead = service.read(organizationId, receiptDTO);

        // Then
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ctReceiptV2><fiscalCode>FC</fiscalCode><paymentAmount>10</paymentAmount><companyName>COMPANY</companyName><debtor><fullName>FULLNAME</fullName></debtor></ctReceiptV2>",
                resultRead);
    }
}
