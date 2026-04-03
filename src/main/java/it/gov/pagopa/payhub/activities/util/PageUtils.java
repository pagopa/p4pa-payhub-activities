package it.gov.pagopa.payhub.activities.util;

import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class PageUtils {
    private PageUtils(){}

    public static int getPageNumber(Pageable pageable) {
        return pageable.isPaged() ? pageable.getPageNumber() : 0;
    }

    public static Integer getPageSize(Pageable pageable) {
        return pageable.isPaged() ? pageable.getPageSize() : null;
    }

    public static List<String> getSortList(Pageable pageable) {
        return pageable.getSort().isSorted()
                ? pageable.getSort().stream()
                        .map(o -> o.getProperty() + "," + o.getDirection()).toList()
                : Collections.emptyList();
    }
}
