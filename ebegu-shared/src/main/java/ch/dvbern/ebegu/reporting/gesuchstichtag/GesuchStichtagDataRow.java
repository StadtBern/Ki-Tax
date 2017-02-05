package ch.dvbern.ebegu.reporting.gesuchstichtag;

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
public class GesuchStichtagDataRow {

	private String bgNummer;
	private String institution;
	private String betreuungsTyp;
	private String periode;
	private Integer nichtFreigegeben;
	private Integer mahnungen;
	private Integer beschwerde;

	public GesuchStichtagDataRow(String bgNummer, String institution, String betreuungsTyp, String periode, Integer nichtFreigegeben, Integer mahnungen, Integer beschwerde) {
		this.bgNummer = bgNummer;
		this.institution = institution;
		this.betreuungsTyp = betreuungsTyp;
		this.periode = periode;
		this.nichtFreigegeben = nichtFreigegeben;
		this.mahnungen = mahnungen;
		this.beschwerde = beschwerde;
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

	public Integer getNichtFreigegeben() {
		return nichtFreigegeben;
	}

	public void setNichtFreigegeben(Integer nichtFreigegeben) {
		this.nichtFreigegeben = nichtFreigegeben;
	}

	public Integer getMahnungen() {
		return mahnungen;
	}

	public void setMahnungen(Integer mahnungen) {
		this.mahnungen = mahnungen;
	}

	public Integer getBeschwerde() {
		return beschwerde;
	}

	public void setBeschwerde(Integer beschwerde) {
		this.beschwerde = beschwerde;
	}
}
