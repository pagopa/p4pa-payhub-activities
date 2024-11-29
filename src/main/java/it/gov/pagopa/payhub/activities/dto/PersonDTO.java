package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO implements Serializable {

    private Character payerUniqueIdentifierTypeChar;
    private String payerUniqueIdentifierType;
    private String payerUniqueIdentifierCode;
    private String payerFullName;
    private String payerAddress;
    private String payerCivic;
    private String payerPostalCode;
    private String payerLocation;
    private String payerProvince;
    private String payerNation;
    private String payerEmail;
}
