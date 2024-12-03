package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.debtposition.GenerateIuvActivityImpl;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.exception.ValueNotValidException;
import it.gov.pagopa.payhub.activities.service.IuvService;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenerateIuvActivityTest {

  @Mock
  private OrganizationService organizationService;

  @Mock
  private IuvService iuvService;

  @InjectMocks
  private GenerateIuvActivityImpl generateIuvActivity;

  private final String VALID_ORG_FISCAL_CODE = "VALID_FISCAL_CODE";
  private final String VALID_ORG_IPA_CODE = "VALID_IPA_CODE";
  private final OrganizationDTO VALID_ORG = OrganizationDTO.builder()
    .orgId(1L)
    .orgFiscalCode(VALID_ORG_FISCAL_CODE)
    .ipaCode(VALID_ORG_IPA_CODE)
    .build();
  private final String VALID_IUV = "12345678901234567";

  private final String INVALID_ORG_FISCAL_CODE = "INVALID_FISCAL_CODE";

  @Test
  void givenValidOrgWhenGenerateIuvThenOk(){
    //Given
    Mockito.when(organizationService.getOrganizationByFiscalCode(VALID_ORG_FISCAL_CODE)).thenReturn(VALID_ORG);
    Mockito.when(iuvService.generateIuv(VALID_ORG)).thenReturn(VALID_IUV);
    //When
    String result = generateIuvActivity.generateIuv(VALID_ORG_FISCAL_CODE);
    //Verify
    Assertions.assertEquals(VALID_IUV, result);
    Mockito.verify(organizationService, Mockito.times(1)).getOrganizationByFiscalCode(VALID_ORG_FISCAL_CODE);
    Mockito.verify(iuvService,Mockito.times(1)).generateIuv(VALID_ORG);
  }

  @Test
  void givenEmptyOrgWhenGenerateIuvThenException(){
    //Verify
    ValueNotValidException exception = Assertions.assertThrows(ValueNotValidException.class, () -> generateIuvActivity.generateIuv(""));
    Assertions.assertEquals("invalid orgFiscalCode", exception.getMessage());
  }

  @Test
  void givenInvalidOrgWhenGenerateIuvThenException(){
    //Given
    Mockito.when(organizationService.getOrganizationByFiscalCode(INVALID_ORG_FISCAL_CODE)).thenReturn(null);
    //Verify
    ValueNotValidException exception = Assertions.assertThrows(ValueNotValidException.class, () -> generateIuvActivity.generateIuv(INVALID_ORG_FISCAL_CODE));
    Assertions.assertEquals("invalid organization", exception.getMessage());
  }
}
