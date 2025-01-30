package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IufClassificationActivityResult {
    private Long organizationId;
    private List<Transfer2ClassifyDTO> transfers2classify;
}
