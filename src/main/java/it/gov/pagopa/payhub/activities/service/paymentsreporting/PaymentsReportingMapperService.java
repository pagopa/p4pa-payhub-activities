package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Service class responsible for data mapping related to `PaymentsReporting` objects.
 */
@Lazy
@Service
public class PaymentsReportingMapperService {

	/**
	 * Maps a `CtFlussoRiversamento` object and an `IngestionFlowFileDTO` into a list of `PaymentsReporting` objects.
	 * Each `PaymentsReporting` in the list corresponds to an individual payment within the flow.
	 *
	 * @param ctFlussoRiversamento the flow data object containing detailed information about the transaction flow,
	 *                             including details for multiple individual payments.
	 * @param ingestionFlowFileDTO the ingestion metadata containing information about the processing flow.
	 * @return a list of `PaymentsReporting` objects, one for each individual payment in the flow.
	 */
	public List<PaymentsReporting> map2PaymentsReportings(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFileDTO ingestionFlowFileDTO) {

		PaymentsReporting.PaymentsReportingBuilder builder = PaymentsReporting.builder()
			.creationDate(OffsetDateTime.now())
			.updateDate(OffsetDateTime.now())
			.acquiringDate(ingestionFlowFileDTO.getCreationDate().atZone(ZoneId.systemDefault()).toLocalDate())
			.organizationId(ingestionFlowFileDTO.getOrg().getOrganizationId())
			.ingestionFlowFileId(ingestionFlowFileDTO.getIngestionFlowFileId())
			.pspIdentifier(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.iuf(ctFlussoRiversamento.getIdentificativoFlusso())
			.flowDateTime(ctFlussoRiversamento.getDataOraFlusso().toGregorianCalendar().toZonedDateTime().toOffsetDateTime())
			.regulationUniqueIdentifier(ctFlussoRiversamento.getIdentificativoUnivocoRegolamento())
			.regulationDate(ctFlussoRiversamento.getDataRegolamento().toGregorianCalendar().toZonedDateTime().toLocalDate())
			.senderPspName(ctFlussoRiversamento.getIstitutoMittente().getDenominazioneMittente())
			.senderPspCode(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getCodiceIdentificativoUnivoco())
			.senderPspType(ctFlussoRiversamento.getIstitutoMittente().getIdentificativoUnivocoMittente().getTipoIdentificativoUnivoco().value())
			.receiverOrganizationName(ctFlussoRiversamento.getIstitutoRicevente().getDenominazioneRicevente())
			.receiverOrganizationCode(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getCodiceIdentificativoUnivoco())
			.receiverOrganizationType(ctFlussoRiversamento.getIstitutoRicevente().getIdentificativoUnivocoRicevente().getTipoIdentificativoUnivoco().value())
			.totalAmountCents(ctFlussoRiversamento.getImportoTotalePagamenti().movePointRight(2).longValueExact())
			.totalPayments(ctFlussoRiversamento.getNumeroTotalePagamenti().longValueExact())
			.bicCodePouringBank(ctFlussoRiversamento.getCodiceBicBancaDiRiversamento());

		return ctFlussoRiversamento.getDatiSingoliPagamenti().stream()
			.map(item -> builder
				.iuv(item.getIdentificativoUnivocoVersamento())
				.iur(item.getIdentificativoUnivocoRiscossione())
				.transferIndex(item.getIndiceDatiSingoloPagamento())
				.amountPaidCents(item.getSingoloImportoPagato().movePointRight(2).longValueExact())
				.paymentOutcomeCode(item.getCodiceEsitoSingoloPagamento())
				.payDate(item.getDataEsitoSingoloPagamento().toGregorianCalendar().toZonedDateTime().toLocalDate())
				.build())
			.toList();
	}

	/**
	 * Maps a `PaymentsReporting` object into `TransferSemanticKeyDTO`.
	 *
	 * @param paymentsReporting the `PaymentsReportingDTO` object containing the data to be mapped.
	 * @return a `TransferSemanticKeyDTO` object containing the mapped data.
	 */
	public TransferSemanticKeyDTO map2TransferSemanticKeyDto(PaymentsReporting paymentsReporting) {
		return TransferSemanticKeyDTO.builder()
			.orgId(paymentsReporting.getOrganizationId())
			.iuv(paymentsReporting.getIuv())
			.iur(paymentsReporting.getIur())
			.transferIndex(paymentsReporting.getTransferIndex())
			.build();
	}
}
