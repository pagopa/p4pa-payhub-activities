package it.gov.pagopa.payhub.activities.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO implements Serializable {

    private Long provinceId;
    private String province;
    private String acronym;
}