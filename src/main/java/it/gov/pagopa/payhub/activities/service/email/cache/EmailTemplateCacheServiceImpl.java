package it.gov.pagopa.payhub.activities.service.email.cache;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.SerializableEmailTemplate;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.mapper.email.EmailTemplateMapper;
import it.gov.pagopa.payhub.activities.util.TemplateEmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@Lazy
@Slf4j
public class EmailTemplateCacheServiceImpl implements EmailTemplateCacheService {

    private final EmailTemplateMapper emailTemplateMapper;
    private final String templateFolderBasePath;
    private final Map<String, Boolean> isTemplateInCacheMap = new HashMap<>();

    public EmailTemplateCacheServiceImpl(
            EmailTemplateMapper emailTemplateMapper,
            @Value("${mail.template.folder-base-path}") String templateFolderBasePath) {
        this.emailTemplateMapper = emailTemplateMapper;
        this.templateFolderBasePath = templateFolderBasePath;
    }

    @Override
    public void saveInCache(EmailTemplate template, String brokerExternalId, EmailTemplateName emailTemplateName) {
        Path templateFolderPath = TemplateEmailUtils.buildTemplateFolderPath(templateFolderBasePath, brokerExternalId, emailTemplateName);
        try (FileOutputStream fos = new FileOutputStream(templateFolderPath.toString());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            if(!Files.isDirectory(templateFolderPath)) {
                Files.createDirectories(templateFolderPath);
            }
            SerializableEmailTemplate serializableEmailTemplate = emailTemplateMapper.mapToSerializable(template);
            oos.writeObject(serializableEmailTemplate);
            isTemplateInCacheMap.put(templateFolderPath.toString(), true);
        } catch (IOException e) {
            log.warn("Error in saving template file \"{}\" into folder \"{}\": {}", emailTemplateName, templateFolderPath.getFileName(), e.getMessage());
            isTemplateInCacheMap.put(templateFolderPath.toString(), false);
        }
    }

    @Override
    public EmailTemplate getFromCache(String brokerExternalId, EmailTemplateName emailTemplateName) {
        Path templateFolderPath = TemplateEmailUtils.buildTemplateFolderPath(templateFolderBasePath, brokerExternalId, emailTemplateName);
        try (FileInputStream fis = new FileInputStream(templateFolderPath.toString());
             ObjectInputStream oos = new ObjectInputStream(fis)) {
            SerializableEmailTemplate serializableEmailTemplate = (SerializableEmailTemplate) oos.readObject();
            return emailTemplateMapper.mapFromSerializable(serializableEmailTemplate);
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Error in reading template file \"{}\" into folder \"{}\": {}", emailTemplateName, templateFolderPath.getFileName(), e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isTemplateInCache(String brokerExternalId, EmailTemplateName emailTemplateName) {
        Path templateFolderPath = TemplateEmailUtils.buildTemplateFolderPath(templateFolderBasePath, brokerExternalId, emailTemplateName);
        return Optional.ofNullable(isTemplateInCacheMap.get(templateFolderPath.toString()))
                .orElse(false);
    }

}
