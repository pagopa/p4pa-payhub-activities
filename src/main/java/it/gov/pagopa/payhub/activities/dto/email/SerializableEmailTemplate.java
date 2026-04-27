package it.gov.pagopa.payhub.activities.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerializableEmailTemplate implements Serializable {
    private String subject;
    private String body;
    private List<SerializableFileResourceDTO> inlines;
}
