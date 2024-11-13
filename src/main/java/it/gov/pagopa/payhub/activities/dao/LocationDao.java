package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.CityDTO;
import it.gov.pagopa.payhub.activities.dto.NationDTO;
import it.gov.pagopa.payhub.activities.dto.ProvinceDTO;

import java.util.Optional;

public interface LocationDao {

    NationDTO getNationByCodeIso(String codeIso);
    ProvinceDTO getProvinceByAcronym(String acronym);
    Optional<CityDTO> getMunicipalityByNameAndProvince(String municipality, String provinceAcronym);


}
