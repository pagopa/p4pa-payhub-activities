package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;

public class IONotificationDTOFaker {

    public static IONotificationDTO buildIONotificationDTO() {
        return TestUtils.getPodamFactory().manufacturePojo(IONotificationDTO.class)
                .serviceId("serviceId")
                .ioTemplateMessage("Descrizione posizione debitoria: %posizioneDebitoria_descrizione%. " +
                        "Nome completo debitore: %debitore_nomeCompleto%. " +
                        "Codice Fiscale debitore: %debitore_codiceFiscale%. " +
                        "Importo totale: %importoTotale% euro. " +
                        "Codice IUV: %IUV%. " +
                        "NAV: %NAV%. " +
                        "Causale: %causale%. " +
                        "Data di esecuzione pagamento: %dataScadenza%.")
                .ioTemplateSubject("subject");
    }

}
