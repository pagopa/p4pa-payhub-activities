package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dao.IuvSequenceNumberDao;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
  classes = {IuvService.class},
  webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
  "iuv.informationSystemId=00"
})
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class IuvServiceTest {

  @MockitoBean
  private IuvSequenceNumberDao iuvSequenceNumberDao;

  @Autowired
  private IuvService iuvService;

  private static final String VALID_ORG_FISCAL_CODE = "VALID_FISCAL_CODE";
  private static final String VALID_ORG_IPA_CODE = "VALID_IPA_CODE";
  private static final String VALID_APPLICATION_CODE = "01";
  private static final Organization VALID_ORG = Organization.builder()
    .organizationId(1L)
    .orgFiscalCode(VALID_ORG_FISCAL_CODE)
    .ipaCode(VALID_ORG_IPA_CODE)
    .applicationCode(VALID_APPLICATION_CODE)
    .build();
  private static final long VALID_PAYMENT_INDEX = 42L;
  private static final String VALID_IUV = "01000000000004285";
  private static final String WRONG_CHECK_IUV = "01000000000004286";
  private static final String WRONG_LENGTH_IUV = "010000000000004285";

  private static final String INVALID_ORG_FISCAL_CODE = "INVALID_FISCAL_CODE";
  private static final String INVALID_ORG_IPA_CODE = "INVALID_IPA_CODE";
  private static final long INVALID_PAYMENT_INDEX = 0L;
  private static final Organization INVALID_ORG = Organization.builder()
    .organizationId(99L)
    .orgFiscalCode(INVALID_ORG_FISCAL_CODE)
    .ipaCode(INVALID_ORG_IPA_CODE)
    .build();

  //region test generateIuv
  @Test
  void givenValidOrgWhenGenerateIuvThenOk(){
    //Given
    Mockito.when(iuvSequenceNumberDao.getNextIuvSequenceNumber(VALID_ORG_IPA_CODE)).thenReturn(VALID_PAYMENT_INDEX);
    //When
    String result = iuvService.generateIuv(VALID_ORG);
    //Verify
    Assertions.assertEquals(VALID_IUV, result);
    Mockito.verify(iuvSequenceNumberDao, Mockito.times(1)).getNextIuvSequenceNumber(VALID_ORG_IPA_CODE);
  }

  @Test
  void givenEmptyOrgWhenGenerateIuvThenException(){
    //Given
    Mockito.when(iuvSequenceNumberDao.getNextIuvSequenceNumber(INVALID_ORG_IPA_CODE)).thenReturn(INVALID_PAYMENT_INDEX);
    //Verify
    Assertions.assertThrows(InvalidValueException.class, () -> iuvService.generateIuv(INVALID_ORG));
  }
  //endregion

  //region test iuv2Nav
  @Test
  void whenIuv2NavThenOk(){
    //When
    String result = iuvService.iuv2Nav(VALID_IUV);
    //Verify
    Assertions.assertEquals(IuvService.AUX_DIGIT+VALID_IUV, result);
  }

  @Test
  void givenInvalidIuvWhenIuv2NavThenException(){
    //Verify
    Assertions.assertThrows(InvalidValueException.class, () -> iuvService.iuv2Nav(WRONG_CHECK_IUV));
  }
  //endregion

  //region test nav2Iuv
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
    Assertions.assertThrows(InvalidValueException.class, () -> iuvService.nav2Iuv("4"+VALID_IUV));
  }
  //endregion

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
