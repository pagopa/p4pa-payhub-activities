package it.gov.pagopa.payhub.activities.service.classifications.trclassifiers;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker.buildTreasuryIuf;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TesNoIufOrIuvClassifierTest {
    private TesNoIufOrIuvClassifier classifier = new TesNoIufOrIuvClassifier();

    @ParameterizedTest
    @MethodSource("provideClassifierScenarios")
    void classificationReturnsExpectedForVariousCombinations(Transfer transfer,
                                                             PaymentNotificationNoPII notification,
                                                             PaymentsReporting reporting,
                                                             TreasuryIuf treasury,
                                                             ClassificationsEnum expected) {
        ClassificationsEnum result = classifier.classify(transfer, null, notification, reporting, treasury);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideClassifierScenarios() {
        Transfer validTransfer = new Transfer();
        PaymentNotificationNoPII validNotification = new PaymentNotificationNoPII();
        PaymentsReporting validReporting = new PaymentsReporting();
        TreasuryIuf validTreasury = buildTreasuryIuf();

        return Stream.of(
            Arguments.of(validTransfer, null, validReporting, validTreasury, null),
            Arguments.of(null, null, validReporting, validTreasury, ClassificationsEnum.TES_NO_IUF_OR_IUV),
            Arguments.of(validTransfer, null, null, validTreasury, ClassificationsEnum.TES_NO_IUF_OR_IUV),
            Arguments.of(validTransfer, validNotification, validReporting, validTreasury, null),
            Arguments.of(validTransfer, null, validReporting, null, null)
        );
    }
}
