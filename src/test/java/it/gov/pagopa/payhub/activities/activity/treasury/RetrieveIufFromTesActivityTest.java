package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryRetrieveIufActivityResult;
import it.gov.pagopa.payhub.activities.exception.RetrieveIufFromTesException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveIufFromTesActivityTest {

    @Mock
    private TreasuryDao treasuryDao;

    private RetrieveIufFromTesActivity retrieveIufFromTesActivity;

    @BeforeEach
    void init() {
        retrieveIufFromTesActivity = new RetrieveIufFromTesActivityImpl(treasuryDao);
    }

    @Test
    void givenTreasuryThenSuccess() {
        String iufToSearch = "IUF_TO_SEARCH";

        TreasuryDTO expectedTreasuryDTO = TreasuryDTO.builder().iuf(iufToSearch).amount(10L).build();
        List<TreasuryDTO> expectedTreasuryDTOS = new ArrayList<>();
        expectedTreasuryDTOS.add(expectedTreasuryDTO);
        TreasuryRetrieveIufActivityResult expectedResult = new TreasuryRetrieveIufActivityResult();

        expectedResult.setReportingDTOList(expectedTreasuryDTOS);
        expectedResult.setSuccess(true);

        when(treasuryDao.searchByIuf(iufToSearch)).thenReturn(expectedTreasuryDTOS);

        TreasuryRetrieveIufActivityResult result = retrieveIufFromTesActivity.searchByIuf(iufToSearch);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenTreasuryThenFailed() {
        String iufToSearch = "";
        TreasuryRetrieveIufActivityResult expectedResult = new TreasuryRetrieveIufActivityResult();
        expectedResult.setReportingDTOList(new ArrayList<>());
        expectedResult.setSuccess(false);

        assertThrows(RetrieveIufFromTesException.class, () ->
                retrieveIufFromTesActivity.searchByIuf(iufToSearch), "IUF is null or blank");
    }
}


