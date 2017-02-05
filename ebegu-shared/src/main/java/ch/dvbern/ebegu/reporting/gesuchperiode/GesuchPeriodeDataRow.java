package ch.dvbern.ebegu.reporting.gesuchperiode;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 31/01/2017.
 */
public class GesuchPeriodeDataRow {

	private String bgNummer;
	private String institution;
	private String betreuungsTyp;
	private String periode;
	private Integer anzahlGesuchOnline;
	private Integer anzahlGesuchPapier;
	private Integer anzahlMutationOnline;
	private Integer anzahlMutationPapier;
	private Integer anzahlMutationAbwesenheit;
	private Integer anzahlMutationBetreuung;
	private Integer anzahlMutationDokumente;
	private Integer anzahlMutationEV;
	private Integer anzahlMutationEwerbspensum;
	private Integer anzahlMutationFamilienSitutation;
	private Integer anzahlMutationFinanzielleSituation;
	private Integer anzahlMutationFreigabe;
	private Integer anzahlMutationGesuchErstellen;
	private Integer anzahlMutationGesuchsteller;
	private Integer anzahlMutationKinder;
	private Integer anzahlMutationUmzug;
	private Integer anzahlMutationVerfuegen;
	private Integer anzahlMahnungen;
	private Integer anzahlBeschwerde;
	private Integer anzahlVerfuegungen;
	private Integer anzahlVerfuegungenNormal;
	private Integer anzahlVerfuegungenMaxEinkommen;
	private Integer anzahlVerfuegungenKeinPensum;
	private Integer anzahlVerfuegungenZuschlagZumPensum;
	private Integer anzahlVerfuegungenNichtEintreten;


	public GesuchPeriodeDataRow(String bgNummer, String institution, String betreuungsTyp, String periode, Integer anzahlGesuchOnline, Integer anzahlGesuchPapier, Integer anzahlMutationOnline, Integer anzahlMutationPapier, Integer anzahlMutationAbwesenheit, Integer anzahlMutationBetreuung, Integer anzahlMutationDokumente, Integer anzahlMutationEV, Integer anzahlMutationEwerbspensum, Integer anzahlMutationFamilienSitutation, Integer anzahlMutationFinanzielleSituation, Integer anzahlMutationFreigabe, Integer anzahlMutationGesuchErstellen, Integer anzahlMutationGesuchsteller, Integer anzahlMutationKinder, Integer anzahlMutationUmzug, Integer anzahlMutationVerfuegen, Integer anzahlMahnungen, Integer anzahlBeschwerde, Integer anzahlVerfuegungen, Integer anzahlVerfuegungenNormal, Integer anzahlVerfuegungenMaxEinkommen, Integer anzahlVerfuegungenKeinPensum, Integer anzahlVerfuegungenZuschlagZumPensum, Integer anzahlVerfuegungenNichtEintreten) {
		this.bgNummer = bgNummer;
		this.institution = institution;
		this.betreuungsTyp = betreuungsTyp;
		this.periode = periode;
		this.anzahlGesuchOnline = anzahlGesuchOnline;
		this.anzahlGesuchPapier = anzahlGesuchPapier;
		this.anzahlMutationOnline = anzahlMutationOnline;
		this.anzahlMutationPapier = anzahlMutationPapier;
		this.anzahlMutationAbwesenheit = anzahlMutationAbwesenheit;
		this.anzahlMutationBetreuung = anzahlMutationBetreuung;
		this.anzahlMutationDokumente = anzahlMutationDokumente;
		this.anzahlMutationEV = anzahlMutationEV;
		this.anzahlMutationEwerbspensum = anzahlMutationEwerbspensum;
		this.anzahlMutationFamilienSitutation = anzahlMutationFamilienSitutation;
		this.anzahlMutationFinanzielleSituation = anzahlMutationFinanzielleSituation;
		this.anzahlMutationFreigabe = anzahlMutationFreigabe;
		this.anzahlMutationGesuchErstellen = anzahlMutationGesuchErstellen;
		this.anzahlMutationGesuchsteller = anzahlMutationGesuchsteller;
		this.anzahlMutationKinder = anzahlMutationKinder;
		this.anzahlMutationUmzug = anzahlMutationUmzug;
		this.anzahlMutationVerfuegen = anzahlMutationVerfuegen;
		this.anzahlMahnungen = anzahlMahnungen;
		this.anzahlBeschwerde = anzahlBeschwerde;
		this.anzahlVerfuegungen = anzahlVerfuegungen;
		this.anzahlVerfuegungenNormal = anzahlVerfuegungenNormal;
		this.anzahlVerfuegungenMaxEinkommen = anzahlVerfuegungenMaxEinkommen;
		this.anzahlVerfuegungenKeinPensum = anzahlVerfuegungenKeinPensum;
		this.anzahlVerfuegungenZuschlagZumPensum = anzahlVerfuegungenZuschlagZumPensum;
		this.anzahlVerfuegungenNichtEintreten = anzahlVerfuegungenNichtEintreten;
	}

	public String getBgNummer() {
		return bgNummer;
	}

	public void setBgNummer(String bgNummer) {
		this.bgNummer = bgNummer;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getBetreuungsTyp() {
		return betreuungsTyp;
	}

	public void setBetreuungsTyp(String betreuungsTyp) {
		this.betreuungsTyp = betreuungsTyp;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public Integer getAnzahlGesuchOnline() {
		return anzahlGesuchOnline;
	}

	public void setAnzahlGesuchOnline(Integer anzahlGesuchOnline) {
		this.anzahlGesuchOnline = anzahlGesuchOnline;
	}

	public Integer getAnzahlGesuchPapier() {
		return anzahlGesuchPapier;
	}

	public void setAnzahlGesuchPapier(Integer anzahlGesuchPapier) {
		this.anzahlGesuchPapier = anzahlGesuchPapier;
	}

	public Integer getAnzahlMutationOnline() {
		return anzahlMutationOnline;
	}

	public void setAnzahlMutationOnline(Integer anzahlMutationOnline) {
		this.anzahlMutationOnline = anzahlMutationOnline;
	}

	public Integer getAnzahlMutationPapier() {
		return anzahlMutationPapier;
	}

	public void setAnzahlMutationPapier(Integer anzahlMutationPapier) {
		this.anzahlMutationPapier = anzahlMutationPapier;
	}

	public Integer getAnzahlMutationAbwesenheit() {
		return anzahlMutationAbwesenheit;
	}

	public void setAnzahlMutationAbwesenheit(Integer anzahlMutationAbwesenheit) {
		this.anzahlMutationAbwesenheit = anzahlMutationAbwesenheit;
	}

	public Integer getAnzahlMutationBetreuung() {
		return anzahlMutationBetreuung;
	}

	public void setAnzahlMutationBetreuung(Integer anzahlMutationBetreuung) {
		this.anzahlMutationBetreuung = anzahlMutationBetreuung;
	}

	public Integer getAnzahlMutationDokumente() {
		return anzahlMutationDokumente;
	}

	public void setAnzahlMutationDokumente(Integer anzahlMutationDokumente) {
		this.anzahlMutationDokumente = anzahlMutationDokumente;
	}

	public Integer getAnzahlMutationEV() {
		return anzahlMutationEV;
	}

	public void setAnzahlMutationEV(Integer anzahlMutationEV) {
		this.anzahlMutationEV = anzahlMutationEV;
	}

	public Integer getAnzahlMutationEwerbspensum() {
		return anzahlMutationEwerbspensum;
	}

	public void setAnzahlMutationEwerbspensum(Integer anzahlMutationEwerbspensum) {
		this.anzahlMutationEwerbspensum = anzahlMutationEwerbspensum;
	}

	public Integer getAnzahlMutationFamilienSitutation() {
		return anzahlMutationFamilienSitutation;
	}

	public void setAnzahlMutationFamilienSitutation(Integer anzahlMutationFamilienSitutation) {
		this.anzahlMutationFamilienSitutation = anzahlMutationFamilienSitutation;
	}

	public Integer getAnzahlMutationFinanzielleSituation() {
		return anzahlMutationFinanzielleSituation;
	}

	public void setAnzahlMutationFinanzielleSituation(Integer anzahlMutationFinanzielleSituation) {
		this.anzahlMutationFinanzielleSituation = anzahlMutationFinanzielleSituation;
	}

	public Integer getAnzahlMutationFreigabe() {
		return anzahlMutationFreigabe;
	}

	public void setAnzahlMutationFreigabe(Integer anzahlMutationFreigabe) {
		this.anzahlMutationFreigabe = anzahlMutationFreigabe;
	}

	public Integer getAnzahlMutationGesuchErstellen() {
		return anzahlMutationGesuchErstellen;
	}

	public void setAnzahlMutationGesuchErstellen(Integer anzahlMutationGesuchErstellen) {
		this.anzahlMutationGesuchErstellen = anzahlMutationGesuchErstellen;
	}

	public Integer getAnzahlMutationGesuchsteller() {
		return anzahlMutationGesuchsteller;
	}

	public void setAnzahlMutationGesuchsteller(Integer anzahlMutationGesuchsteller) {
		this.anzahlMutationGesuchsteller = anzahlMutationGesuchsteller;
	}

	public Integer getAnzahlMutationKinder() {
		return anzahlMutationKinder;
	}

	public void setAnzahlMutationKinder(Integer anzahlMutationKinder) {
		this.anzahlMutationKinder = anzahlMutationKinder;
	}

	public Integer getAnzahlMutationUmzug() {
		return anzahlMutationUmzug;
	}

	public void setAnzahlMutationUmzug(Integer anzahlMutationUmzug) {
		this.anzahlMutationUmzug = anzahlMutationUmzug;
	}

	public Integer getAnzahlMutationVerfuegen() {
		return anzahlMutationVerfuegen;
	}

	public void setAnzahlMutationVerfuegen(Integer anzahlMutationVerfuegen) {
		this.anzahlMutationVerfuegen = anzahlMutationVerfuegen;
	}

	public Integer getAnzahlMahnungen() {
		return anzahlMahnungen;
	}

	public void setAnzahlMahnungen(Integer anzahlMahnungen) {
		this.anzahlMahnungen = anzahlMahnungen;
	}

	public Integer getAnzahlBeschwerde() {
		return anzahlBeschwerde;
	}

	public void setAnzahlBeschwerde(Integer anzahlBeschwerde) {
		this.anzahlBeschwerde = anzahlBeschwerde;
	}

	public Integer getAnzahlVerfuegungen() {
		return anzahlVerfuegungen;
	}

	public void setAnzahlVerfuegungen(Integer anzahlVerfuegungen) {
		this.anzahlVerfuegungen = anzahlVerfuegungen;
	}

	public Integer getAnzahlVerfuegungenNormal() {
		return anzahlVerfuegungenNormal;
	}

	public void setAnzahlVerfuegungenNormal(Integer anzahlVerfuegungenNormal) {
		this.anzahlVerfuegungenNormal = anzahlVerfuegungenNormal;
	}

	public Integer getAnzahlVerfuegungenMaxEinkommen() {
		return anzahlVerfuegungenMaxEinkommen;
	}

	public void setAnzahlVerfuegungenMaxEinkommen(Integer anzahlVerfuegungenMaxEinkommen) {
		this.anzahlVerfuegungenMaxEinkommen = anzahlVerfuegungenMaxEinkommen;
	}

	public Integer getAnzahlVerfuegungenKeinPensum() {
		return anzahlVerfuegungenKeinPensum;
	}

	public void setAnzahlVerfuegungenKeinPensum(Integer anzahlVerfuegungenKeinPensum) {
		this.anzahlVerfuegungenKeinPensum = anzahlVerfuegungenKeinPensum;
	}

	public Integer getAnzahlVerfuegungenZuschlagZumPensum() {
		return anzahlVerfuegungenZuschlagZumPensum;
	}

	public void setAnzahlVerfuegungenZuschlagZumPensum(Integer anzahlVerfuegungenZuschlagZumPensum) {
		this.anzahlVerfuegungenZuschlagZumPensum = anzahlVerfuegungenZuschlagZumPensum;
	}

	public Integer getAnzahlVerfuegungenNichtEintreten() {
		return anzahlVerfuegungenNichtEintreten;
	}

	public void setAnzahlVerfuegungenNichtEintreten(Integer anzahlVerfuegungenNichtEintreten) {
		this.anzahlVerfuegungenNichtEintreten = anzahlVerfuegungenNichtEintreten;
	}
}
