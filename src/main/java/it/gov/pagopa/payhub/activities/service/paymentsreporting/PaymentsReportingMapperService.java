package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtDatiSingoliPagamenti;
import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

@Lazy
@Service
public class PaymentsReportingMapperService implements BiFunction<CtFlussoRiversamento, IngestionFlowFileDTO, PaymentsReportingDTO> {

	@Override
	public PaymentsReportingDTO apply(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFileDTO ingestionFlowFileDTO) {
		return PaymentsReportingDTO.builder()
			.orgId(ingestionFlowFileDTO.getOrg())
			.ingestionFlowFile(ingestionFlowFileDTO)
			.idPsp(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.flowIdentifierCode(ctFlussoRiversamento.getIdentificativoFlusso())
			.flowDateTime(ctFlussoRiversamento.getDataOraFlusso().toGregorianCalendar().getTime())
			.regulationUniqueIdentifier(ctFlussoRiversamento.getIdentificativoUnivocoRegolamento())
			.regulationDate(ctFlussoRiversamento.getDataRegolamento().toGregorianCalendar().getTime())
			.senderPspName(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.senderPspCode(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getCodiceIdentificativoUnivoco())
			.senderPspType(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getTipoIdentificativoUnivoco().value())
			.receiverOrganizationName(ctFlussoRiversamento.getIstitutoRicevente().getDenominazioneRicevente())
			.receiverOrganizationId(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco())
			.receiverOrganizationType(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getTipoIdentificativoUnivoco().value())
			.totalPayments(ctFlussoRiversamento.getDatiSingoliPagamenti().size())
			.sumPayments(ctFlussoRiversamento.getNumeroTotalePagamenti())
			.amountPaid(ctFlussoRiversamento.getImportoTotalePagamenti())
			.bicCodePouringBank(ctFlussoRiversamento.getCodiceBicBancaDiRiversamento())
			.build();
	}

	public PaymentsReportingDTO toBuilder(PaymentsReportingDTO paymentsReportingDTO, CtDatiSingoliPagamenti singlePaymetdData) {
		return paymentsReportingDTO.toBuilder()
			.creditorReferenceId(singlePaymetdData.getIdentificativoUnivocoVersamento())
			.regulationId(singlePaymetdData.getIdentificativoUnivocoRiscossione())
			.transferIndex(singlePaymetdData.getIndiceDatiSingoloPagamento())
			.amountPaid(singlePaymetdData.getSingoloImportoPagato())
			.paymentOutcomeCode(singlePaymetdData.getCodiceEsitoSingoloPagamento())
			.payDate(singlePaymetdData.getDataEsitoSingoloPagamento().toGregorianCalendar().getTime())
			.build();
	}
}
