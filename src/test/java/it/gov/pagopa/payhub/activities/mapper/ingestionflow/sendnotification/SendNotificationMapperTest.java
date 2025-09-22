package it.gov.pagopa.payhub.activities.mapper.ingestionflow.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification.SendNotificationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.sendnotification.dto.generated.CreateNotificationRequest;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static it.gov.pagopa.payhub.activities.util.faker.SendNotificationFaker.buildSendNotificationIngestionFlowFileDTO;

@ExtendWith(MockitoExtension.class)
class SendNotificationMapperTest {

    private SendNotificationMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new SendNotificationMapper();
    }

    @Test
    void givenFullDtoWhenBuildRequestThenAllFieldsMapped() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertNotNull(result);
        TestUtils.checkNotNullFields(result);
    }

    @Test
    void givenNoDigitalDomicileWhenBuildRequestThenRecipientHasNoDigitalAddress() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();
        dto.setDigitalDomicileAddress(null);
        dto.setDigitalDomicileType(null);

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertNull(result.getRecipients().getFirst().getDigitalDomicile());
    }

    @Test
    void givenNoPagoPaNoF24ThenPaymentsListEmpty() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();
        dto.setPayment(null);
        dto.setF24Payment1(null);

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertTrue(result.getRecipients().getFirst().getPayments().isEmpty());
    }

    @Test
    void givenPagoPaWithoutAttachmentThenBuildPagoPaStillWorks() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();
        dto.setF24Payment1(null);
        dto.setAttachment(null);

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertNotNull(result.getRecipients().getFirst().getPayments().getFirst().getPagoPa());
        Assertions.assertNull(result.getRecipients().getFirst().getPayments().getFirst().getPagoPa().getAttachment());
    }

    @Test
    void givenOnlyF24WhenBuildRequestThenPagoPaIsNullButF24Present() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();
        dto.setPayment(null);
        dto.setMetadataAttachment1(null);

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertNull(Objects.requireNonNull(result.getRecipients().getFirst().getPayments()).getFirst().getPagoPa());
        Assertions.assertNull(Objects.requireNonNull(result.getRecipients().getFirst().getPayments()).getFirst().getF24().getMetadataAttachment());
        Assertions.assertNotNull(result.getRecipients().getFirst().getPayments().getFirst().getF24());
    }

    @Test
    void givenDocumentEmptyWhenBuildRequestThenMapDocumentIsNull() {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();
        dto.setDocument(new ArrayListValuedHashMap<>());

        CreateNotificationRequest result = mapper.buildCreateNotificationRequest(dto);

        Assertions.assertEquals(result.getDocuments(), List.of());
        TestUtils.checkNotNullFields(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void givenIndexOutOfRangeWhenGetPaymentByIndexThenReturnNull(String index) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SendNotificationIngestionFlowFileDTO dto = buildSendNotificationIngestionFlowFileDTO();

        Method method = SendNotificationMapper.class.getDeclaredMethod("getPaymentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
        if (Objects.equals(index, "2")) {
            method = SendNotificationMapper.class.getDeclaredMethod("getAttachmentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
        } else if (Objects.equals(index, "3")) {
            method = SendNotificationMapper.class.getDeclaredMethod("getF24PaymentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
        } else if (Objects.equals(index, "4")) {
            method = SendNotificationMapper.class.getDeclaredMethod("getMetadataAttachmentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
        } else if (Objects.equals(index, "5")) {
            method = SendNotificationMapper.class.getDeclaredMethod("getDocumentByIndex", SendNotificationIngestionFlowFileDTO.class, int.class);
        }
        method.setAccessible(true);

        MultiValuedMap<String, String> result = (MultiValuedMap<String, String>) method.invoke(mapper, dto, 6);

        Assertions.assertNull(result);
    }

}