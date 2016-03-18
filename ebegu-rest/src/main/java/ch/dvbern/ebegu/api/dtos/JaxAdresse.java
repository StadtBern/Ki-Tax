package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
@XmlRootElement(name = "adresse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAdresse extends JaxAbstractDTO {

	private static final long serialVersionUID = -2249803693436143445L;

	@NotNull
	private String strasse;
	private String hausnummer;
	@NotNull
	private String plz;
	@NotNull
	private String ort;
	private String gemeinde;
	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigAb;
	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigBis;


	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public String getGemeinde() {
		return gemeinde;
	}

	public void setGemeinde(String gemeinde) {
		this.gemeinde = gemeinde;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	@Nullable
	public LocalDate getGueltigAb() { return gueltigAb; }

	public void setGueltigAb(@Nullable LocalDate gueltigAb) { this.gueltigAb = gueltigAb; }

	@Nullable
	public LocalDate getGueltigBis() { return gueltigBis; }

	public void setGueltigBis(@Nullable LocalDate gueltigBis) {	this.gueltigBis = gueltigBis; }

}
