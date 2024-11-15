package it.gov.pagopa.payhub.activities.dto.position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements Serializable {

    private Long municipalityId;
    private String municipality;
    private Long provinceId;
}