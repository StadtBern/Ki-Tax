package ch.dvbern.ebegu.dto.dataexport.v1;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Verfuegung}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class VerfuegungExportDTO {

	private String refnr;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate von;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate bis;

	private int version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime verfuegtAm;

	private KindExportDTO kind;

	private GesuchstellerExportDTO gesuchsteller;

	private BetreuungExportDTO betreuung;

	private List<ZeitabschnittExportDTO> zeitabschnitte;

	private List<ZeitabschnittExportDTO> ignorierteZeitabschnitte;

	public String getRefnr() {
		return refnr;
	}

	public void setRefnr(String refnr) {
		this.refnr = refnr;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public LocalDateTime getVerfuegtAm() {
		return verfuegtAm;
	}

	public void setVerfuegtAm(LocalDateTime verfuegtAm) {
		this.verfuegtAm = verfuegtAm;
	}

	public KindExportDTO getKind() {
		return kind;
	}

	public void setKind(KindExportDTO kind) {
		this.kind = kind;
	}

	public GesuchstellerExportDTO getGesuchsteller() {
		return gesuchsteller;
	}

	public void setGesuchsteller(GesuchstellerExportDTO gesuchsteller) {
		this.gesuchsteller = gesuchsteller;
	}

	public BetreuungExportDTO getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(BetreuungExportDTO betreuung) {
		this.betreuung = betreuung;
	}

	public List<ZeitabschnittExportDTO> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(List<ZeitabschnittExportDTO> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	public List<ZeitabschnittExportDTO> getIgnorierteZeitabschnitte() {
		return ignorierteZeitabschnitte;
	}

	public void setIgnorierteZeitabschnitte(List<ZeitabschnittExportDTO> ignorierteZeitabschnitte) {
		this.ignorierteZeitabschnitte = ignorierteZeitabschnitte;
	}

	@SuppressWarnings("OverlyComplexBooleanExpression")
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VerfuegungExportDTO that = (VerfuegungExportDTO) o;
		return getVersion() == that.getVersion() &&
			Objects.equals(getRefnr(), that.getRefnr()) &&
			Objects.equals(getVon(), that.getVon()) &&
			Objects.equals(getBis(), that.getBis()) &&
			Objects.equals(getVerfuegtAm(), that.getVerfuegtAm()) &&
			Objects.equals(getKind(), that.getKind()) &&
			Objects.equals(getGesuchsteller(), that.getGesuchsteller()) &&
			Objects.equals(getBetreuung(), that.getBetreuung()) &&
			Objects.equals(getZeitabschnitte(), that.getZeitabschnitte()) &&
			Objects.equals(getIgnorierteZeitabschnitte(), that.getIgnorierteZeitabschnitte());
	}

	@Override
	public int hashCode() {
		return Objects.hash(refnr, von, bis, version, verfuegtAm, kind, gesuchsteller, betreuung, zeitabschnitte);
	}
}
