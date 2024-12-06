package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.exception.InvalidDataException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;

@Lazy
@Service
public class FieldsValitatorService {

	public Date validateDatePattern(XMLGregorianCalendar xmlGregorianCalendar) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();
		if (!format.equals(date)) {
			throw new InvalidDataException("Invalid date pattern");
		}
		return date;
	}


}
