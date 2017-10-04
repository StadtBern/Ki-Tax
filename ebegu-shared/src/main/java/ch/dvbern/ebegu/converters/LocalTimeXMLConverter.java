package ch.dvbern.ebegu.converters;

import java.time.LocalTime;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 * Konvertiert ein LocalDate Java 8 Objekt in einen String fuer JSON
 */
@XmlJavaTypeAdapter(value = LocalDateXMLConverter.class, type = LocalTime.class)
public class LocalTimeXMLConverter extends XmlAdapter<String, LocalTime>  {

	@Nullable
	@Override
	public LocalTime unmarshal(String v) {
		return StringUtils.isEmpty(v) ? null : LocalTime.parse(v);
	}

	@Nullable
	@Override
	public String marshal(LocalTime v) {
		return v == null ? null : v.toString();
	}
}
