package it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for data mapping related to `PaymentsReporting` objects.
 */
@Lazy
@Service
public class PaymentsReportingMapperService {

	/**
	 * Maps a `CtFlussoRiversamento` object and an `IngestionFlowFile` into a list of `PaymentsReporting` objects.
	 * Each `PaymentsReporting` in the list corresponds to an individual payment within the flow.
	 *
	 * @param ctFlussoRiversamento the flow data object containing detailed information about the transaction flow,
	 *                             including details for multiple individual payments.
	 * @param ingestionFlowFileDTO the ingestion metadata containing information about the processing flow.
	 * @return a list of `PaymentsReporting` objects, one for each individual payment in the flow.
	 */
	public List<PaymentsReporting> map2PaymentsReportings(CtFlussoRiversamento ctFlussoRiversamento, IngestionFlowFile ingestionFlowFileDTO) {

		PaymentsReporting.PaymentsReportingBuilder builder = PaymentsReporting.builder()
			.creationDate(OffsetDateTime.now())
			.updateDate(OffsetDateTime.now())
			.acquiringDate(ingestionFlowFileDTO.getCreationDate().toLocalDate())
			.organizationId(ingestionFlowFileDTO.getOrganizationId())
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
			.bicCodePouringBank(ctFlussoRiversamento.getCodiceBicBancaDiRiversamento())
			.revision(Optional.ofNullable(ctFlussoRiversamento.getRevisioneFlusso()).orElse(0));

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
	 * Maps a `PaymentsReporting` object into `PaymentsReportingTransferDTO`.
	 *
	 * @param paymentsReporting the `PaymentsReportingDTO` object containing the data to be mapped.
	 * @return a `PaymentsReportingTransferDTO` object containing the mapped data.
	 */
	public PaymentsReportingTransferDTO map(PaymentsReporting paymentsReporting) {
		return PaymentsReportingTransferDTO.builder()
			.orgId(paymentsReporting.getOrganizationId())
			.iuv(paymentsReporting.getIuv())
			.iur(paymentsReporting.getIur())
			.transferIndex(paymentsReporting.getTransferIndex())
			.paymentOutcomeCode(paymentsReporting.getPaymentOutcomeCode())
			.build();
	}
}
