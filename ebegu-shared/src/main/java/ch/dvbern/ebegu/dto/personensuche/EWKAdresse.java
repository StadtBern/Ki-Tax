
package ch.dvbern.ebegu.dto.personensuche;

import java.time.LocalDate;


/**
 * DTO für Adressen aus dem EWK
 */
public class EWKAdresse {

    protected String adresstyp;
    protected String adresstypTxt;
    protected LocalDate gueltigVon;
    protected LocalDate gueltigBis;
    protected String coName;
    protected String postfach;
    protected String bfSGemeinde;
    protected String strasse;
    protected String hausnummer;
    protected String postleitzahl;
    protected String ort;
    protected String kanton;
    protected String land;

	public String getAdresstyp() {
		return adresstyp;
	}

	public void setAdresstyp(String adresstyp) {
		this.adresstyp = adresstyp;
	}

	public String getAdresstypTxt() {
		return adresstypTxt;
	}

	public void setAdresstypTxt(String adresstypTxt) {
		this.adresstypTxt = adresstypTxt;
	}

	public LocalDate getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(LocalDate gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getPostfach() {
		return postfach;
	}

	public void setPostfach(String postfach) {
		this.postfach = postfach;
	}

	public String getBfSGemeinde() {
		return bfSGemeinde;
	}

	public void setBfSGemeinde(String bfSGemeinde) {
		this.bfSGemeinde = bfSGemeinde;
	}

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

	public String getPostleitzahl() {
		return postleitzahl;
	}

	public void setPostleitzahl(String postleitzahl) {
		this.postleitzahl = postleitzahl;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getKanton() {
		return kanton;
	}

	public void setKanton(String kanton) {
		this.kanton = kanton;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}
}
