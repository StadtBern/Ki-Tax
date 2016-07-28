package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LandConverter;
import ch.dvbern.ebegu.entities.AdresseTyp;
import ch.dvbern.ebegu.enums.Land;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
@XmlRootElement(name = "adresse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAdresse extends JaxAbstractDateRangedDTO {


	private static final long serialVersionUID = -1093677998323618626L;

	@NotNull
	private String strasse;
	private String hausnummer;

	private String zusatzzeile;
	@NotNull
	private String plz;
	@NotNull
	private String ort;

	@NotNull
	@XmlJavaTypeAdapter(LandConverter.class)
	private Land land = Land.CH;

	private String gemeinde;

	private AdresseTyp adresseTyp;

	@Nullable
	private String organisation;

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

	public String getZusatzzeile() {
		return zusatzzeile;
	}

	public void setZusatzzeile(String zusatzzeile) {
		this.zusatzzeile = zusatzzeile;
	}

	public Land getLand() {
		return land;
	}

	public void setLand(Land land) {
		this.land = land;
	}

	public void setAdresseTyp(AdresseTyp adresseTyp) {
		this.adresseTyp = adresseTyp;
	}

	public AdresseTyp getAdresseTyp() {
		return adresseTyp;
	}

	@Nullable
	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(@Nullable String organisation) {
		this.organisation = organisation;
	}
}
