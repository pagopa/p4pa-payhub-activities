package it.gov.pagopa.payhub.activities.util.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCsv {
    @CsvBindByName(column = "Column1", required = true)
    private String column1;
    @CsvBindByName(column = "Column2", required = true)
    private String column2;
    @CsvBindByName(column = "Column3")
    @CsvDate(value = "yyyy-MM-dd")
    private LocalDate column3;
    @CsvIgnore
    private Long lineNumber;
}
