package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dao.OrganizationDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

@SpringBootTest(
  classes = {OrganizationService.class},
  webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableConfigurationProperties
class OrganizationServiceTest {

  @MockitoBean
  private OrganizationDao organizationDao;

  @Autowired
  private OrganizationService organizationService;

  private static final String VALID_ORG_FISCAL_CODE = "VALID_FISCAL_CODE";
  private static final String VALID_ORG_IPA_CODE = "VALID_IPA_CODE";
  private static final String VALID_APPLICATION_CODE = "01";
  private static final Optional<OrganizationDTO> VALID_ORG = Optional.of(OrganizationDTO.builder()
    .orgId(1L)
    .orgFiscalCode(VALID_ORG_FISCAL_CODE)
    .ipaCode(VALID_ORG_IPA_CODE)
    .applicationCode(VALID_APPLICATION_CODE)
    .build());
  private static final String INVALID_ORG_FISCAL_CODE = "INVALID_FISCAL_CODE";

  @Test
  void givenValidOrgWhenGetOrganizationByFiscalCodeThenFound(){
    //given
    Mockito.when(organizationDao.getOrganizationByFiscalCode(VALID_ORG_FISCAL_CODE)).thenReturn(VALID_ORG);
    //when
    Optional<OrganizationDTO> result = organizationService.getOrganizationByFiscalCode(VALID_ORG_FISCAL_CODE);
    //verify
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(VALID_ORG, result);
    Mockito.verify(organizationDao, Mockito.times(1)).getOrganizationByFiscalCode(VALID_ORG_FISCAL_CODE);
  }

  @Test
  void givenInvalidOrgWhenGetOrganizationByFiscalCodeThenEmpty(){
    //given
    Mockito.when(organizationDao.getOrganizationByFiscalCode(INVALID_ORG_FISCAL_CODE)).thenReturn(Optional.empty());
    //when
    Optional<OrganizationDTO> result = organizationService.getOrganizationByFiscalCode(INVALID_ORG_FISCAL_CODE);
    //verify
    Assertions.assertTrue(result.isEmpty());
    Mockito.verify(organizationDao, Mockito.times(1)).getOrganizationByFiscalCode(INVALID_ORG_FISCAL_CODE);
  }
}
