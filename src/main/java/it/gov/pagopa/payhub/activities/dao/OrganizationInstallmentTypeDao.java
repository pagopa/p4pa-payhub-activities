package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.OrganizationInstallmentTypeDTO;

import java.util.List;

public interface OrganizationInstallmentTypeDao {

    List<OrganizationInstallmentTypeDTO> getByMygovEnteIdAndOperatoreUsername(Long mygovEnteId, String operatoreUsername);
}
