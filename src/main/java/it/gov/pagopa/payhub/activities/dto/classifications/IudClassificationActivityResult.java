package it.gov.pagopa.payhub.activities.dto.classifications;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IudClassificationActivityResult {
    private Long organizationId;
    private List<Transfer2ClassifyDTO> transfers2classify;
}
