package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.dto.address.CityDTO;
import it.gov.pagopa.payhub.activities.dto.address.NationDTO;
import it.gov.pagopa.payhub.activities.dto.address.ProvinceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO implements Serializable {

    private String uniqueIdentifierType;
    private String uniqueIdentifierCode;
    private boolean flagAnonymousData;
    private String fullName;
    private String address;
    private String civic;
    private String postalCode;
    private CityDTO location;
    private ProvinceDTO province;
    private NationDTO nation;
    private String email;
}
