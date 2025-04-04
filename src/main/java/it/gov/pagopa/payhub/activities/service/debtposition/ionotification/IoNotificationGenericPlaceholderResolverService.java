package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.IoNotificationPlaceholderUtils.ITALIAN_DATE_FORMAT;
import static it.gov.pagopa.payhub.activities.util.IoNotificationPlaceholderUtils.applyPlaceholder;
import static it.gov.pagopa.payhub.activities.util.Utilities.centsAmountToEuroString;

@Service
@Lazy
public class IoNotificationGenericPlaceholderResolverService {

    public String applyDefaultPlaceholder(String markdown, DebtPositionDTO debtPositionDTO, InstallmentDTO installment) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%posizioneDebitoria_descrizione%", Objects.toString(debtPositionDTO.getDescription(), ""));
        placeholders.put("%debitore_nomeCompleto%", Objects.toString(installment.getDebtor().getFullName(), ""));
        placeholders.put("%debitore_codiceFiscale%", Objects.toString(installment.getDebtor().getFiscalCode(), ""));
        placeholders.put("%importoTotale%", Objects.toString(centsAmountToEuroString(installment.getAmountCents()), ""));
        placeholders.put("%IUV%", Objects.toString(installment.getIuv(), ""));
        placeholders.put("%NAV%", Objects.toString(installment.getNav(), ""));
        placeholders.put("%causale%", Objects.toString(installment.getRemittanceInformation(), ""));

        String formattedDueDate = installment.getDueDate() != null ? installment.getDueDate().format(ITALIAN_DATE_FORMAT) : "";
        placeholders.put("%dataScadenza%", formattedDueDate);

        return applyPlaceholder(markdown, placeholders);
    }
}
