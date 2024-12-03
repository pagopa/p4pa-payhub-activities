package it.gov.pagopa.payhub.activities.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    private Long userId;
    private String externalUserId;
}
