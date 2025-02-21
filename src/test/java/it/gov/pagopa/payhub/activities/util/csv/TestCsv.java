package it.gov.pagopa.payhub.activities.util.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCsv {
    @CsvBindByName(column = "Column1", required = true)
    private String column1;
    @CsvBindByName(column = "Column2", required = true)
    private String column2;
    @CsvCustomBindByName(column = "Column3", converter = CsvOffsetDateTimeConverter.class)
    private OffsetDateTime column3;
    @CsvIgnore
    private Long lineNumber;
}
