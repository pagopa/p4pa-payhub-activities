package it.gov.pagopa.payhub.activities.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix= "email.templates")
@Data
public class EmailTemplatesConfiguration {
    private EmailTemplate paymentsReportingFlowOk;
    private EmailTemplate paymentsReportingFlowKo;
/*
    @Value("email.templates.payments-reporting-flow-ok.subject")
    private String subjectOk;
    @Value("email.templates.payments-reporting-flow-ok.subject")
    private String subjectKo;
    @Value("email.templates.payments-reporting-flow-ok.body")
    private String bodyOk;
    @Value("email.templates.payments-reporting-flow-ko.body")
    private String bodyKo;

    /*

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

    private Map<String, String> publicKeys;

    public Map<String, PublicKey> getPublicKeysAsMap() {
        return Optional.ofNullable(publicKeys)
                .orElse(Map.of())
                .entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> getPublicKeyFromString(entry.getKey(), entry.getValue())
                ));
    }

    private PublicKey getPublicKeyFromString(String keyName, String encodedKey) {
        try {
            X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(publicKeyX509);
        } catch (Exception e){
            throw new Exception("invalid public key for: " + keyName);
        }
    }
    */


}