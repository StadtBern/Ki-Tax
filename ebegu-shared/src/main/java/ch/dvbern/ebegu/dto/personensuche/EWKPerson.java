
package ch.dvbern.ebegu.dto.personensuche;

import ch.dvbern.ebegu.enums.Geschlecht;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * DTO für Personen aus dem EWK
 */
public class EWKPerson {

	private String personID;
	private List<EWKEinwohnercode> einwohnercodes = new ArrayList<>();
	private String nachname;
	private String ledigname;
	private String vorname;
	private String rufname;
	private LocalDate geburtsdatum;
	private LocalDate zuzugsdatum;
	private String nationalitaet;
	private String zivilstand;
	private String zivilstandTxt;
	private LocalDate zivilstandsdatum;
	private Geschlecht geschlecht;
	private String bewilligungsart;
	private String bewilligungsartTxt;
	private LocalDate bewilligungBis;
	private List<EWKAdresse> adressen = new ArrayList<>();

    private List<EWKBeziehung> beziehungen = new ArrayList<>();


	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public List<EWKEinwohnercode> getEinwohnercodes() {
		return einwohnercodes;
	}

	public void setEinwohnercodes(List<EWKEinwohnercode> einwohnercodes) {
		this.einwohnercodes = einwohnercodes;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getLedigname() {
		return ledigname;
	}

	public void setLedigname(String ledigname) {
		this.ledigname = ledigname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getRufname() {
		return rufname;
	}

	public void setRufname(String rufname) {
		this.rufname = rufname;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public LocalDate getZuzugsdatum() {
		return zuzugsdatum;
	}

	public void setZuzugsdatum(LocalDate zuzugsdatum) {
		this.zuzugsdatum = zuzugsdatum;
	}

	public String getNationalitaet() {
		return nationalitaet;
	}

	public void setNationalitaet(String nationalitaet) {
		this.nationalitaet = nationalitaet;
	}

	public String getZivilstand() {
		return zivilstand;
	}

	public void setZivilstand(String zivilstand) {
		this.zivilstand = zivilstand;
	}

	public String getZivilstandTxt() {
		return zivilstandTxt;
	}

	public void setZivilstandTxt(String zivilstandTxt) {
		this.zivilstandTxt = zivilstandTxt;
	}

	public LocalDate getZivilstandsdatum() {
		return zivilstandsdatum;
	}

	public void setZivilstandsdatum(LocalDate zivilstandsdatum) {
		this.zivilstandsdatum = zivilstandsdatum;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getBewilligungsart() {
		return bewilligungsart;
	}

	public void setBewilligungsart(String bewilligungsart) {
		this.bewilligungsart = bewilligungsart;
	}

	public String getBewilligungsartTxt() {
		return bewilligungsartTxt;
	}

	public void setBewilligungsartTxt(String bewilligungsartTxt) {
		this.bewilligungsartTxt = bewilligungsartTxt;
	}

	public LocalDate getBewilligungBis() {
		return bewilligungBis;
	}

	public void setBewilligungBis(LocalDate bewilligungBis) {
		this.bewilligungBis = bewilligungBis;
	}

	public List<EWKAdresse> getAdressen() {
		return adressen;
	}

	public void setAdressen(List<EWKAdresse> adressen) {
		this.adressen = adressen;
	}

	public List<EWKBeziehung> getBeziehungen() {
		return beziehungen;
	}

	public void setBeziehungen(List<EWKBeziehung> beziehungen) {
		this.beziehungen = beziehungen;
	}
}
