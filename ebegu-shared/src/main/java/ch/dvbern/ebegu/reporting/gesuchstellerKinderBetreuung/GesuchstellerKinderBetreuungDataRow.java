package ch.dvbern.ebegu.reporting.gesuchstellerKinderBetreuung;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;

import java.math.BigDecimal;
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
 * Created by medu on 31/01/2017.
 */
public class GesuchstellerKinderBetreuungDataRow {

	private String bgNummer;
	private String institution;
	private BetreuungsangebotTyp betreuungsTyp;
	private String periode;

	private LocalDate eingangsdatum;
	private LocalDate verfuegungsdatum;
	private Integer fallId;

	private String gs1Name;
	private String gs1Vorname;
	private String gs1Strasse;
	private String gs1Hausnummer;
	private String gs1Zusatzzeile;
	private String gs1Plz;
	private String gs1Ort;
	private String gs1EwkId;
	private Boolean gs1Diplomatenstatus;
	private Integer gs1EwpAngestellt;
	private Integer gs1EwpAusbildung;
	private Integer gs1EwpSelbstaendig;
	private Integer gs1EwpRav;
	private Integer gs1EwpZuschlag;
	private Integer gs1EwpGesundhtl;

	private String gs2Name;
	private String gs2Vorname;
	private String gs2Strasse;
	private String gs2Hausnummer;
	private String gs2Zusatzzeile;
	private String gs2Plz;
	private String gs2Ort;
	private String gs2EwkId;
	private Boolean gs2Diplomatenstatus;
	private Integer gs2EwpAngestellt;
	private Integer gs2EwpAusbildung;
	private Integer gs2EwpSelbstaendig;
	private Integer gs2EwpRav;
	private Integer gs2EwpZuschlag;
	private Integer gs2EwpGesundhtl;

	private EnumFamilienstatus familiensituation;
	private EnumGesuchstellerKardinalitaet kardinalitaet;
	private BigDecimal familiengroesse;

	private BigDecimal massgEinkVorFamilienabzug;
	private BigDecimal familienabzug;
	private BigDecimal massgEink;
	private Integer einkommensjahr;
	private Boolean ekvVorhanden;
	private Boolean stvGeprueft;
	private Boolean veranlagt;

	private String kindName;
	private String kindVorname;
	private LocalDate kindGeburtsdatum;
	private Boolean kindFachstelle;
	private Boolean kindErwBeduerfnisse;
	private Boolean kindDeutsch;
	private Boolean kindEingeschult;

	private LocalDate zeitabschnittVon;
	private LocalDate zeitabschnittBis;
	private BigDecimal betreuungsPensum;
	private BigDecimal anspruchsPensum;
	private BigDecimal bgPensum;
	private BigDecimal bgStunden;
	private BigDecimal vollkosten;
	private BigDecimal elternbeitrag;
	private BigDecimal verguenstigt;


	public GesuchstellerKinderBetreuungDataRow() {
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

	public BetreuungsangebotTyp getBetreuungsTyp() {
		return betreuungsTyp;
	}

	public void setBetreuungsTyp(BetreuungsangebotTyp betreuungsTyp) {
		this.betreuungsTyp = betreuungsTyp;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public LocalDate getVerfuegungsdatum() {
		return verfuegungsdatum;
	}

	public void setVerfuegungsdatum(LocalDate verfuegungsdatum) {
		this.verfuegungsdatum = verfuegungsdatum;
	}

	public Integer getFallId() {
		return fallId;
	}

	public void setFallId(Integer fallId) {
		this.fallId = fallId;
	}

	public String getGs1Name() {
		return gs1Name;
	}

	public void setGs1Name(String gs1Name) {
		this.gs1Name = gs1Name;
	}

	public String getGs1Vorname() {
		return gs1Vorname;
	}

	public void setGs1Vorname(String gs1Vorname) {
		this.gs1Vorname = gs1Vorname;
	}

	public String getGs1Strasse() {
		return gs1Strasse;
	}

	public void setGs1Strasse(String gs1Strasse) {
		this.gs1Strasse = gs1Strasse;
	}

	public String getGs1Hausnummer() {
		return gs1Hausnummer;
	}

	public void setGs1Hausnummer(String gs1Hausnummer) {
		this.gs1Hausnummer = gs1Hausnummer;
	}

	public String getGs1Zusatzzeile() {
		return gs1Zusatzzeile;
	}

	public void setGs1Zusatzzeile(String gs1Zusatzzeile) {
		this.gs1Zusatzzeile = gs1Zusatzzeile;
	}

	public String getGs1Plz() {
		return gs1Plz;
	}

	public void setGs1Plz(String gs1Plz) {
		this.gs1Plz = gs1Plz;
	}

	public String getGs1Ort() {
		return gs1Ort;
	}

	public void setGs1Ort(String gs1Ort) {
		this.gs1Ort = gs1Ort;
	}

	public String getGs1EwkId() {
		return gs1EwkId;
	}

	public void setGs1EwkId(String gs1EwkId) {
		this.gs1EwkId = gs1EwkId;
	}

	public Boolean getGs1Diplomatenstatus() {
		return gs1Diplomatenstatus;
	}

	public void setGs1Diplomatenstatus(Boolean gs1Diplomatenstatus) {
		this.gs1Diplomatenstatus = gs1Diplomatenstatus;
	}

	public Integer getGs1EwpAngestellt() {
		return gs1EwpAngestellt;
	}

	public void setGs1EwpAngestellt(Integer gs1EwpAngestellt) {
		this.gs1EwpAngestellt = gs1EwpAngestellt;
	}

	public Integer getGs1EwpAusbildung() {
		return gs1EwpAusbildung;
	}

	public void setGs1EwpAusbildung(Integer gs1EwpAusbildung) {
		this.gs1EwpAusbildung = gs1EwpAusbildung;
	}

	public Integer getGs1EwpSelbstaendig() {
		return gs1EwpSelbstaendig;
	}

	public void setGs1EwpSelbstaendig(Integer gs1EwpSelbstaendig) {
		this.gs1EwpSelbstaendig = gs1EwpSelbstaendig;
	}

	public Integer getGs1EwpRav() {
		return gs1EwpRav;
	}

	public void setGs1EwpRav(Integer gs1EwpRav) {
		this.gs1EwpRav = gs1EwpRav;
	}

	public Integer getGs1EwpZuschlag() {
		return gs1EwpZuschlag;
	}

	public void setGs1EwpZuschlag(Integer gs1EwpZuschlag) {
		this.gs1EwpZuschlag = gs1EwpZuschlag;
	}

	public Integer getGs1EwpGesundhtl() {
		return gs1EwpGesundhtl;
	}

	public void setGs1EwpGesundhtl(Integer gs1EwpGesundhtl) {
		this.gs1EwpGesundhtl = gs1EwpGesundhtl;
	}

	public String getGs2Name() {
		return gs2Name;
	}

	public void setGs2Name(String gs2Name) {
		this.gs2Name = gs2Name;
	}

	public String getGs2Vorname() {
		return gs2Vorname;
	}

	public void setGs2Vorname(String gs2Vorname) {
		this.gs2Vorname = gs2Vorname;
	}

	public String getGs2Strasse() {
		return gs2Strasse;
	}

	public void setGs2Strasse(String gs2Strasse) {
		this.gs2Strasse = gs2Strasse;
	}

	public String getGs2Hausnummer() {
		return gs2Hausnummer;
	}

	public void setGs2Hausnummer(String gs2Hausnummer) {
		this.gs2Hausnummer = gs2Hausnummer;
	}

	public String getGs2Zusatzzeile() {
		return gs2Zusatzzeile;
	}

	public void setGs2Zusatzzeile(String gs2Zusatzzeile) {
		this.gs2Zusatzzeile = gs2Zusatzzeile;
	}

	public String getGs2Plz() {
		return gs2Plz;
	}

	public void setGs2Plz(String gs2Plz) {
		this.gs2Plz = gs2Plz;
	}

	public String getGs2Ort() {
		return gs2Ort;
	}

	public void setGs2Ort(String gs2Ort) {
		this.gs2Ort = gs2Ort;
	}

	public String getGs2EwkId() {
		return gs2EwkId;
	}

	public void setGs2EwkId(String gs2EwkId) {
		this.gs2EwkId = gs2EwkId;
	}

	public Boolean getGs2Diplomatenstatus() {
		return gs2Diplomatenstatus;
	}

	public void setGs2Diplomatenstatus(Boolean gs2Diplomatenstatus) {
		this.gs2Diplomatenstatus = gs2Diplomatenstatus;
	}

	public Integer getGs2EwpAngestellt() {
		return gs2EwpAngestellt;
	}

	public void setGs2EwpAngestellt(Integer gs2EwpAngestellt) {
		this.gs2EwpAngestellt = gs2EwpAngestellt;
	}

	public Integer getGs2EwpAusbildung() {
		return gs2EwpAusbildung;
	}

	public void setGs2EwpAusbildung(Integer gs2EwpAusbildung) {
		this.gs2EwpAusbildung = gs2EwpAusbildung;
	}

	public Integer getGs2EwpSelbstaendig() {
		return gs2EwpSelbstaendig;
	}

	public void setGs2EwpSelbstaendig(Integer gs2EwpSelbstaendig) {
		this.gs2EwpSelbstaendig = gs2EwpSelbstaendig;
	}

	public Integer getGs2EwpRav() {
		return gs2EwpRav;
	}

	public void setGs2EwpRav(Integer gs2EwpRav) {
		this.gs2EwpRav = gs2EwpRav;
	}

	public Integer getGs2EwpZuschlag() {
		return gs2EwpZuschlag;
	}

	public void setGs2EwpZuschlag(Integer gs2EwpZuschlag) {
		this.gs2EwpZuschlag = gs2EwpZuschlag;
	}

	public Integer getGs2EwpGesundhtl() {
		return gs2EwpGesundhtl;
	}

	public void setGs2EwpGesundhtl(Integer gs2EwpGesundhtl) {
		this.gs2EwpGesundhtl = gs2EwpGesundhtl;
	}

	public EnumFamilienstatus getFamiliensituation() {
		return familiensituation;
	}

	public void setFamiliensituation(EnumFamilienstatus familiensituation) {
		this.familiensituation = familiensituation;
	}

	public EnumGesuchstellerKardinalitaet getKardinalitaet() {
		return kardinalitaet;
	}

	public void setKardinalitaet(EnumGesuchstellerKardinalitaet kardinalitaet) {
		this.kardinalitaet = kardinalitaet;
	}

	public BigDecimal getFamiliengroesse() {
		return familiengroesse;
	}

	public void setFamiliengroesse(BigDecimal familiengroesse) {
		this.familiengroesse = familiengroesse;
	}

	public BigDecimal getMassgEinkVorFamilienabzug() {
		return massgEinkVorFamilienabzug;
	}

	public void setMassgEinkVorFamilienabzug(BigDecimal massgEinkVorFamilienabzug) {
		this.massgEinkVorFamilienabzug = massgEinkVorFamilienabzug;
	}

	public BigDecimal getFamilienabzug() {
		return familienabzug;
	}

	public void setFamilienabzug(BigDecimal familienabzug) {
		this.familienabzug = familienabzug;
	}

	public BigDecimal getMassgEink() {
		return massgEink;
	}

	public void setMassgEink(BigDecimal massgEink) {
		this.massgEink = massgEink;
	}

	public Integer getEinkommensjahr() {
		return einkommensjahr;
	}

	public void setEinkommensjahr(Integer einkommensjahr) {
		this.einkommensjahr = einkommensjahr;
	}

	public Boolean getEkvVorhanden() {
		return ekvVorhanden;
	}

	public void setEkvVorhanden(Boolean ekvVorhanden) {
		this.ekvVorhanden = ekvVorhanden;
	}

	public Boolean getStvGeprueft() {
		return stvGeprueft;
	}

	public void setStvGeprueft(Boolean stvGeprueft) {
		this.stvGeprueft = stvGeprueft;
	}

	public Boolean getVeranlagt() {
		return veranlagt;
	}

	public void setVeranlagt(Boolean veranlagt) {
		this.veranlagt = veranlagt;
	}

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	public String getKindVorname() {
		return kindVorname;
	}

	public void setKindVorname(String kindVorname) {
		this.kindVorname = kindVorname;
	}

	public LocalDate getKindGeburtsdatum() {
		return kindGeburtsdatum;
	}

	public void setKindGeburtsdatum(LocalDate kindGeburtsdatum) {
		this.kindGeburtsdatum = kindGeburtsdatum;
	}

	public Boolean getKindFachstelle() {
		return kindFachstelle;
	}

	public void setKindFachstelle(Boolean kindFachstelle) {
		this.kindFachstelle = kindFachstelle;
	}

	public Boolean getKindErwBeduerfnisse() {
		return kindErwBeduerfnisse;
	}

	public void setKindErwBeduerfnisse(Boolean kindErwBeduerfnisse) {
		this.kindErwBeduerfnisse = kindErwBeduerfnisse;
	}

	public Boolean getKindDeutsch() {
		return kindDeutsch;
	}

	public void setKindDeutsch(Boolean kindDeutsch) {
		this.kindDeutsch = kindDeutsch;
	}

	public Boolean getKindEingeschult() {
		return kindEingeschult;
	}

	public void setKindEingeschult(Boolean kindEingeschult) {
		this.kindEingeschult = kindEingeschult;
	}

	public LocalDate getZeitabschnittVon() {
		return zeitabschnittVon;
	}

	public void setZeitabschnittVon(LocalDate zeitabschnittVon) {
		this.zeitabschnittVon = zeitabschnittVon;
	}

	public LocalDate getZeitabschnittBis() {
		return zeitabschnittBis;
	}

	public void setZeitabschnittBis(LocalDate zeitabschnittBis) {
		this.zeitabschnittBis = zeitabschnittBis;
	}

	public BigDecimal getBetreuungsPensum() {
		return betreuungsPensum;
	}

	public void setBetreuungsPensum(BigDecimal betreuungsPensum) {
		this.betreuungsPensum = betreuungsPensum;
	}

	public BigDecimal getAnspruchsPensum() {
		return anspruchsPensum;
	}

	public void setAnspruchsPensum(BigDecimal anspruchsPensum) {
		this.anspruchsPensum = anspruchsPensum;
	}

	public BigDecimal getBgPensum() {
		return bgPensum;
	}

	public void setBgPensum(BigDecimal bgPensum) {
		this.bgPensum = bgPensum;
	}

	public BigDecimal getBgStunden() {
		return bgStunden;
	}

	public void setBgStunden(BigDecimal bgStunden) {
		this.bgStunden = bgStunden;
	}

	public BigDecimal getVollkosten() {
		return vollkosten;
	}

	public void setVollkosten(BigDecimal vollkosten) {
		this.vollkosten = vollkosten;
	}

	public BigDecimal getElternbeitrag() {
		return elternbeitrag;
	}

	public void setElternbeitrag(BigDecimal elternbeitrag) {
		this.elternbeitrag = elternbeitrag;
	}

	public BigDecimal getVerguenstigt() {
		return verguenstigt;
	}

	public void setVerguenstigt(BigDecimal verguenstigt) {
		this.verguenstigt = verguenstigt;
	}
}
