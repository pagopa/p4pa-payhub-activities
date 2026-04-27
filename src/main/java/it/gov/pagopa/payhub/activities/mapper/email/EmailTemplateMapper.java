package it.gov.pagopa.payhub.activities.mapper.email;

import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.dto.email.SerializableEmailTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class EmailTemplateMapper {

    private final FileResourceMapper fileResourceMapper;

    public EmailTemplateMapper(FileResourceMapper fileResourceMapper) {
        this.fileResourceMapper = fileResourceMapper;
    }


    public SerializableEmailTemplate mapToSerializable(EmailTemplate emailTemplate) {
        return new SerializableEmailTemplate(
                emailTemplate.getSubject(),
                emailTemplate.getBody(),
                fileResourceMapper.mapToSerializable(emailTemplate.getInlines())
        );
    }

    public EmailTemplate mapFromSerializable(SerializableEmailTemplate serializableEmailTemplate) {
        return new EmailTemplate(
                serializableEmailTemplate.getSubject(),
                serializableEmailTemplate.getBody(),
                fileResourceMapper.mapFromSerializable(serializableEmailTemplate.getInlines())
        );
    }
}
