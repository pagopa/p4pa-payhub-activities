package it.gov.pagopa.payhub.activities.service.email.remote.cache;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Lazy
@Slf4j
public class RemoteEmailTemplateCacheServiceImpl implements RemoteEmailTemplateCacheService {

    private final String templateFolderBasePath;

    public RemoteEmailTemplateCacheServiceImpl(
            @Value("${folders.email-template}") String templateFolderBasePath) {
        this.templateFolderBasePath = templateFolderBasePath;
    }

    @Override
    public void saveInCache(EmailTemplate template, String brokerExternalId, EmailTemplateName emailTemplateName) {
        Path templateFolderPath = buildTemplateFolderPath(brokerExternalId, emailTemplateName);
        if(!Files.isDirectory(templateFolderPath)) {
            createDirectory(templateFolderPath);
        }
        File templateFile = buildTemplateFile(templateFolderPath, emailTemplateName);
        try (FileOutputStream fos = new FileOutputStream(templateFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(template);
        } catch (IllegalStateException | IOException e) {
            log.error("Error in saving template file \"{}\" into folder \"{}\": {}", emailTemplateName, templateFolderPath.getFileName(), e.getMessage());
        }
    }

    @Override
    public EmailTemplate getFromCache(String brokerExternalId, EmailTemplateName emailTemplateName) {
        Path templateFolderPath = buildTemplateFolderPath(brokerExternalId, emailTemplateName);
        File templateFile = buildTemplateFile(templateFolderPath, emailTemplateName);
        if(!templateFile.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(templateFile);
             ObjectInputStream oos = new ObjectInputStream(fis)) {
            return (EmailTemplate) oos.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error in reading template file {} into folder {}: {}", emailTemplateName, templateFolderPath.getFileName(), e.getMessage());
            return null;
        }
    }

    private Path buildTemplateFolderPath(String brokerExternalId, EmailTemplateName templateName) {
        return Path.of(templateFolderBasePath, brokerExternalId, templateName.name());
    }

    private File buildTemplateFile(Path templateFolderPath, EmailTemplateName templateName) {
        return templateFolderPath.resolve(templateName.name()).toFile();
    }

    private static void createDirectory(Path templateFolderPath) {
        try {
            Files.createDirectories(templateFolderPath);
        } catch (IOException e) {
            log.error("Error in creating new directory {}: {}", templateFolderPath, e.getMessage());
        }
    }

}
