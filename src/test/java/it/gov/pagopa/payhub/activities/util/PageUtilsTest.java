package it.gov.pagopa.payhub.activities.util;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.CollectionUtils;

class PageUtilsTest {
    @Test
    void givenPageWhenGetPageNumberThenOk(){
        int expectedPageNumber = 2;
        PageRequest pageRequest = PageRequest.of(expectedPageNumber, 10);

        int pageNumber = PageUtils.getPageNumber(pageRequest);

        Assertions.assertEquals(expectedPageNumber,pageNumber);
    }

    @Test
    void givenUnpagedWhenGetPageNumberThenDefaultValue(){
        int expectedPageNumber = 0;

        int pageNumber = PageUtils.getPageNumber(Pageable.unpaged());

        Assertions.assertEquals(expectedPageNumber,pageNumber);
    }

    @Test
    void givenPageWhenGetPageSizeThenOk(){
        int expectedPageSize = 10;
        PageRequest pageRequest = PageRequest.of(2, expectedPageSize);

        Integer pageSize = PageUtils.getPageSize(pageRequest);

        Assertions.assertEquals(expectedPageSize,pageSize);
    }

    @Test
    void givenUnpagedWhenGetPageSizeThenNull(){
        Integer pageSize = PageUtils.getPageSize(Pageable.unpaged());

        Assertions.assertNull(pageSize);
    }

    @Test
    void givenSortWhenGetSortListThenOk(){
        List<String> expectedSortList = List.of("sort1,ASC","sort2,DESC");
        List<String> sortList = PageUtils.getSortList(PageRequest.of(0, 10,
                Sort.by(List.of(Order.asc("sort1"), Order.desc("sort2")))));

        Assertions.assertFalse(CollectionUtils.isEmpty(sortList));
        Assertions.assertEquals(expectedSortList,sortList);
    }

    @Test
    void givenUnsortedWhenGetSortListThenOk(){
        List<String> sortList = PageUtils.getSortList(PageRequest.of(0, 10));

        Assertions.assertTrue(CollectionUtils.isEmpty(sortList));
    }
}
