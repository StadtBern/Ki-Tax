package ch.dvbern.ebegu.dto.dataexport.v1;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ZeitabschnittExportDTO {

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate von;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate bis;

	//betreuungspensum
	private int effektiveBetreuungPct;

	//Anspruch
	private int anspruchPct;

	//BG Pensum
	private int verguenstigtPct;

	private BigDecimal vollkosten;

	private BigDecimal verguenstigung;

	public ZeitabschnittExportDTO(LocalDate von, LocalDate bis, int effektiveBetr, int anspruchPct, int vergPct, BigDecimal vollkosten, BigDecimal verguenstigung) {
		this.von = von;
		this.bis = bis;
		this.effektiveBetreuungPct = effektiveBetr;
		this.anspruchPct = anspruchPct;
		this.verguenstigtPct = vergPct;
		this.vollkosten = vollkosten;
		this.verguenstigung = verguenstigung;

	}

	public ZeitabschnittExportDTO() {
	}

	public LocalDate getVon() {
		return von;
	}

	public void setVon(LocalDate von) {
		this.von = von;
	}

	public LocalDate getBis() {
		return bis;
	}

	public void setBis(LocalDate bis) {
		this.bis = bis;
	}

	public int getEffektiveBetreuungPct() {
		return effektiveBetreuungPct;
	}

	public void setEffektiveBetreuungPct(int effektiveBetreuungPct) {
		this.effektiveBetreuungPct = effektiveBetreuungPct;
	}

	public int getAnspruchPct() {
		return anspruchPct;
	}

	public void setAnspruchPct(int anspruchPct) {
		this.anspruchPct = anspruchPct;
	}

	public int getVerguenstigtPct() {
		return verguenstigtPct;
	}

	public void setVerguenstigtPct(int verguenstigtPct) {
		this.verguenstigtPct = verguenstigtPct;
	}

	public BigDecimal getVollkosten() {
		return vollkosten;
	}

	public void setVollkosten(BigDecimal vollkosten) {
		this.vollkosten = vollkosten;
	}

	public BigDecimal getVerguenstigung() {
		return verguenstigung;
	}

	public void setVerguenstigung(BigDecimal verguenstigung) {
		this.verguenstigung = verguenstigung;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ZeitabschnittExportDTO that = (ZeitabschnittExportDTO) o;
		return getEffektiveBetreuungPct() == that.getEffektiveBetreuungPct() &&
			getAnspruchPct() == that.getAnspruchPct() &&
			getVerguenstigtPct() == that.getVerguenstigtPct() &&
			Objects.equals(getVon(), that.getVon()) &&
			Objects.equals(getBis(), that.getBis()) &&
			Objects.equals(getVollkosten(), that.getVollkosten()) &&
			Objects.equals(getVerguenstigung(), that.getVerguenstigung());
	}

	@Override
	public int hashCode() {
		return Objects.hash(von, bis, effektiveBetreuungPct, anspruchPct, verguenstigtPct, vollkosten, verguenstigung);
	}
}
