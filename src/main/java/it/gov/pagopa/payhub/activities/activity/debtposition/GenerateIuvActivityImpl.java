package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.exception.ValueNotValidException;
import it.gov.pagopa.payhub.activities.service.IuvService;
import it.gov.pagopa.payhub.activities.service.OrganizationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class GenerateIuvActivityImpl implements GenerateIuvActivity{

  private final OrganizationService organizationService;

  private final IuvService iuvService;

  public GenerateIuvActivityImpl(OrganizationService organizationService, IuvService iuvService){
    this.organizationService = organizationService;
    this.iuvService = iuvService;
  }


  @Override
  public String generateIuv(String orgFiscalCode) {
    if(StringUtils.isBlank(orgFiscalCode)){
      throw new ValueNotValidException("invalid orgFiscalCode");
    }
    OrganizationDTO org = organizationService.getOrganizationByFiscalCode(orgFiscalCode);
    if(org==null){
      throw new ValueNotValidException("invalid organization");
    }
    String iuv = iuvService.generateIuv(org);
    
    return iuv;
  }
}
