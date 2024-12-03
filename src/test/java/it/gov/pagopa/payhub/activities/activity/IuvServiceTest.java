package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.dao.ProgressiviVersamentoDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.exception.ValueNotValidException;
import it.gov.pagopa.payhub.activities.service.IuvService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
  classes = {IuvService.class},
  webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
  "iuv.informationSystemId=00"
})
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
public class IuvServiceTest {

  @MockBean
  private ProgressiviVersamentoDao progressiviVersamentoDao;

  @Autowired
  private IuvService iuvService;

  private final String VALID_ORG_FISCAL_CODE = "VALID_FISCAL_CODE";
  private final String VALID_ORG_IPA_CODE = "VALID_IPA_CODE";
  private final String VALID_APPLICATION_CODE = "01";
  private final OrganizationDTO VALID_ORG = OrganizationDTO.builder()
    .orgId(1L)
    .orgFiscalCode(VALID_ORG_FISCAL_CODE)
    .ipaCode(VALID_ORG_IPA_CODE)
    .applicationCode(VALID_APPLICATION_CODE)
    .build();
  private final long VALID_PAYMENT_INDEX = 42L;
  private final String VALID_IUV = "01000000000004285";
  private final String WRONG_CHECK_IUV = "01000000000004286";
  private final String WRONG_LENGTH_IUV = "010000000000004285";

  private final String INVALID_ORG_FISCAL_CODE = "INVALID_FISCAL_CODE";
  private final String INVALID_ORG_IPA_CODE = "INVALID_IPA_CODE";
  private final long INVALID_PAYMENT_INDEX = 0L;
  private final OrganizationDTO INVALID_ORG = OrganizationDTO.builder()
    .orgId(99L)
    .orgFiscalCode(INVALID_ORG_FISCAL_CODE)
    .ipaCode(INVALID_ORG_IPA_CODE)
    .build();

  //region test generateIuv
  @Test
  void givenValidOrgWhenGenerateIuvThenOk(){
    //Given
    Mockito.when(progressiviVersamentoDao.getNextProgressivoVersamento(VALID_ORG_IPA_CODE)).thenReturn(VALID_PAYMENT_INDEX);
    //When
    String result = iuvService.generateIuv(VALID_ORG);
    //Verify
    Assertions.assertEquals(VALID_IUV, result);
    Mockito.verify(progressiviVersamentoDao, Mockito.times(1)).getNextProgressivoVersamento(VALID_ORG_IPA_CODE);
  }

  @Test
  void givenEmptyOrgWhenGenerateIuvThenException(){
    //Given
    Mockito.when(progressiviVersamentoDao.getNextProgressivoVersamento(INVALID_ORG_IPA_CODE)).thenReturn(INVALID_PAYMENT_INDEX);
    //Verify
    Assertions.assertThrows(ValueNotValidException.class, () -> iuvService.generateIuv(INVALID_ORG));
  }
  //endregion

  @Test
  void whenIuv2NavThenOk(){
    //When
    String result = iuvService.iuv2Nav(VALID_IUV);
    //Verify
    Assertions.assertEquals(IuvService.AUX_DIGIT+VALID_IUV, result);
  }

  @Test
  void givenValidNavWhenNav2IuvThenOk(){
    //When
    String result = iuvService.nav2Iuv(IuvService.AUX_DIGIT+VALID_IUV);
    //Verify
    Assertions.assertEquals(VALID_IUV, result);
  }

  @Test
  void givenInvalidNavWhenNav2IuvThenException(){
    //Verify
    Assertions.assertThrows(ValueNotValidException.class, () -> iuvService.nav2Iuv("4"+VALID_IUV));
  }

  //region test isValidNav
  @Test
  void givenValidNavWhenIsValidNavThenOk(){
    //When
    boolean result = iuvService.isValidNav(IuvService.AUX_DIGIT+VALID_IUV);
    //Verify
    Assertions.assertTrue(result);
  }

  @Test
  void givenInvalidNavWhenIsValidNavThenException(){
    //When
    boolean result = iuvService.isValidNav("4"+VALID_IUV);
    //Verify
    Assertions.assertFalse(result);
  }

  @Test
  void givenWrongLengthNavWhenIsValidNavThenException(){
    //When
    boolean result = iuvService.isValidNav(IuvService.AUX_DIGIT+WRONG_LENGTH_IUV);
    //Verify
    Assertions.assertFalse(result);
  }

  @Test
  void givenWrongCheckDigitNavWhenIsValidNavThenException(){
    //When
    boolean result = iuvService.isValidNav(IuvService.AUX_DIGIT+WRONG_CHECK_IUV);
    //Verify
    Assertions.assertFalse(result);
  }

  @Test
  void givenNotNumericNavWhenIsValidNavThenException(){
    //When
    boolean result = iuvService.isValidNav(IuvService.AUX_DIGIT+"NOT_NUMERIC_12345");
    //Verify
    Assertions.assertFalse(result);
  }
  //endregion
}
