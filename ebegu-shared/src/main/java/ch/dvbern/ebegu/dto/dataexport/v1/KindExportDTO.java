package ch.dvbern.ebegu.dto.dataexport.v1;

import java.time.LocalDate;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateXMLConverter;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Kind}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class KindExportDTO {

	private String vorname;
	private String nachname;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate geburtsdatum;


	public KindExportDTO() {
	}

	public KindExportDTO(String vorname, String nachname, LocalDate geburtsdatum) {
		this.vorname = vorname;
		this.nachname = nachname;
		this.geburtsdatum = geburtsdatum;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		KindExportDTO that = (KindExportDTO) o;
		return Objects.equals(getVorname(), that.getVorname()) &&
			Objects.equals(getNachname(), that.getNachname()) &&
			Objects.equals(getGeburtsdatum(), that.getGeburtsdatum());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getVorname(), getNachname(), getGeburtsdatum());
	}
}
