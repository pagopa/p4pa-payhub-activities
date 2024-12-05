package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dao.IuvSequenceNumberDao;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.exception.ValueNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Service class exposing methods related to IUV handling.
 */
@Lazy
@Service
@Slf4j
public class IuvService {

  private static final String PROGRESSIVO_PADDING_ZEROES_SMALL = "%011d";
  public static final String AUX_DIGIT = "3";

  private final String informationSystemId;

  private final IuvSequenceNumberDao iuvSequenceNumberDao;

  public IuvService(@Value("${iuv.informationSystemId:00}") String informationSystemId,
                    IuvSequenceNumberDao iuvSequenceNumberDao) {
    this.informationSystemId = informationSystemId;
    this.iuvSequenceNumberDao = iuvSequenceNumberDao;
  }


  /**
   * Generate a valid and unique IUV given the organization entity.
   * @param org the organization for which to generate the IUV
   * @return the generated IUV
   */
  public String generateIuv(OrganizationDTO org){
    StringBuilder iuvBuilder = new StringBuilder();
    iuvBuilder.append(org.getApplicationCode());
    iuvBuilder.append(informationSystemId);
    long paymentIndex = iuvSequenceNumberDao.getNextIuvSequenceNumber(org.getIpaCode());
    if(paymentIndex<1){
      log.error("invalid payment index returned for org[{}/{}]: {}", org.getIpaCode(), org.getOrgFiscalCode(), paymentIndex);
      throw new ValueNotValidException("invalid payment index");
    }
    iuvBuilder.append(String.format(PROGRESSIVO_PADDING_ZEROES_SMALL, paymentIndex));

    //check digit
    String digitString = AUX_DIGIT + iuvBuilder;
    long digit = Long.parseLong(digitString);
    long reminder = digit % 93;
    String checkDigit = StringUtils.leftPad(String.valueOf(reminder), 2, '0');
    iuvBuilder.append(checkDigit);

    return iuvBuilder.toString();
  }

  /**
   * Utility method to generate the NAV (notice number) given the corresponding IUV.
   * @param iuv the IUV for which to generate the NAV
   * @return the generated NAV
   */
  public String iuv2Nav(String iuv){
    if(isValidIuv(iuv))
      return AUX_DIGIT + iuv;
    else
      throw new ValueNotValidException("invalid iuv");
  }

  /**
   * Utility method to extract the IUV given the corresponding NAV (notice number).
   * @param nav the NAV for which to extract the IUV
   * @return the extraxted IUV
   */
  public String nav2Iuv(String nav){
    if(isValidNav(nav)){
      return nav.substring(AUX_DIGIT.length());
    } else {
      throw new ValueNotValidException("invalid nav");
    }
  }

  /**
   * Utility method to formally validate a IUV.
   * @param iuv the IUV to validate
   * @return true if valid, otherwise false
   */
  public boolean isValidIuv(String iuv){
    return isValidNav(StringUtils.join(AUX_DIGIT+iuv));
  }

  /**
   * Utility method to formally validate a NAV.
   * @param nav the NAV to validate
   * @return true if valid, otherwise false
   */
  public boolean isValidNav(String nav){
    if(StringUtils.length(nav)==18 && StringUtils.startsWith(nav, AUX_DIGIT)){
      try{
        return Long.parseLong(nav.substring(0,16)) % 93 == Long.parseLong(nav.substring(16));
      }catch(Exception e){
        return false;
      }
    }
    return false;
  }
}
