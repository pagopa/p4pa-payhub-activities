package it.gov.pagopa.payhub.activities.mapper.email;

import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.dto.email.SerializableFileResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Lazy
@Component
public class FileResourceMapper {

    public FileResourceDTO mapFromSerializable(SerializableFileResourceDTO serializableResourceDTO) {
        return new FileResourceDTO(
                new ByteArrayResource(serializableResourceDTO.getFileContent()),
                serializableResourceDTO.getFileName()
        );
    }

    public SerializableFileResourceDTO mapToSerializable(FileResourceDTO resourceDTO) {
        try {
            return new SerializableFileResourceDTO(
                    resourceDTO.getResource().getContentAsByteArray(),
                    resourceDTO.getFileName()
            );
        } catch (IOException e) {
            log.warn("Error in serializing object FileResourceDTO with file name \"{}\": {}", resourceDTO.getFileName(), e.getMessage());
            throw new IllegalStateException("Error in serializing object FileResourceDTO with file name \"%s\"".formatted(resourceDTO.getFileName()), e);
        }
    }

    public List<FileResourceDTO> mapFromSerializable(List<SerializableFileResourceDTO> serializableResourceDTOList) {
        return serializableResourceDTOList.stream()
                .map(this::mapFromSerializable)
                .toList();
    }

    public List<SerializableFileResourceDTO> mapToSerializable(List<FileResourceDTO> resourceDTOList) {
        return resourceDTOList.stream()
                .map(this::mapToSerializable)
                .toList();
    }
}
