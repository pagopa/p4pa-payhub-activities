package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.position.CityDTO;
import it.gov.pagopa.payhub.activities.dto.position.NationDTO;
import it.gov.pagopa.payhub.activities.dto.position.ProvinceDTO;

import java.util.Optional;

public interface PositionDao {

    /**
     * It will return NationDTO related to the input codeIso
     **/
    NationDTO getNationByCodeIso(String codeIso);

    /**
     * It will return ProvinceDTO related to the input acronym
     **/
    ProvinceDTO getProvinceByAcronym(String acronym);

    /**
     * It will return CityDTO with the requested name and province acronym
     **/
    Optional<CityDTO> getMunicipalityByNameAndProvince(String municipality, String provinceAcronym);

}