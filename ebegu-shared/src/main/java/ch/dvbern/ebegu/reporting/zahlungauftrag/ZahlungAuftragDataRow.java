package ch.dvbern.ebegu.reporting.zahlungauftrag;


import java.time.LocalDate;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 21/02/2017.
 */
public class ZahlungAuftragDataRow {

	private String institution;
	private String name;
	private String vorname;
	private LocalDate gebDatum;
	private String verfuegung;
	private LocalDate vonDatum;
	private LocalDate bisDatum;
	private String bgPensum;
	private double betragCHF;

	//TODO: delete this class if we end up using the Entities in toExcelMergerDTO

	public ZahlungAuftragDataRow(String institution, String name, String vorname, LocalDate gebDatum, String verfuegung, LocalDate vonDatum, LocalDate bisDatum, String bgPensum, double betragCHF) {
		this.institution = institution;
		this.name = name;
		this.vorname = vorname;
		this.gebDatum = gebDatum;
		this.verfuegung = verfuegung;
		this.vonDatum = vonDatum;
		this.bisDatum = bisDatum;
		this.bgPensum = bgPensum;
		this.betragCHF = betragCHF;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public LocalDate getGebDatum() {
		return gebDatum;
	}

	public void setGebDatum(LocalDate gebDatum) {
		this.gebDatum = gebDatum;
	}

	public String getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(String verfuegung) {
		this.verfuegung = verfuegung;
	}

	public LocalDate getVonDatum() {
		return vonDatum;
	}

	public void setVonDatum(LocalDate vonDatum) {
		this.vonDatum = vonDatum;
	}

	public LocalDate getBisDatum() {
		return bisDatum;
	}

	public void setBisDatum(LocalDate bisDatum) {
		this.bisDatum = bisDatum;
	}

	public String getBgPensum() {
		return bgPensum;
	}

	public void setBgPensum(String bgPensum) {
		this.bgPensum = bgPensum;
	}

	public double getBetragCHF() {
		return betragCHF;
	}

	public void setBetragCHF(double betragCHF) {
		this.betragCHF = betragCHF;
	}
}
