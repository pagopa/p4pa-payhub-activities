package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationDTO implements Serializable {
    private Long nationId;
    private String nationName;
    private String codeIsoAlpha2;

//    public boolean hasProvince(){
//        return StringUtils.equalsIgnoreCase(codiceIsoAlpha2, "it");
//    }
}