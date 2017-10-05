/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.reporting.gesuchzeitraum;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GesuchZeitraumDataRow {

	private String bgNummer;
	private Integer gesuchLaufNr;
	private String institution;
	private String betreuungsTyp;
	private String periode;
	private Integer anzahlGesuchOnline;
	private Integer anzahlGesuchPapier;
	private Integer anzahlMutationOnline;
	private Integer anzahlMutationPapier;
	private Integer anzahlMutationAbwesenheit;
	private Integer anzahlMutationBetreuung;
	private Integer anzahlMutationEV;
	private Integer anzahlMutationEwerbspensum;
	private Integer anzahlMutationFamilienSitutation;
	private Integer anzahlMutationFinanzielleSituation;
	private Integer anzahlMutationGesuchsteller;
	private Integer anzahlMutationKinder;
	private Integer anzahlMutationUmzug;
	private Integer anzahlMahnungen;
	private Integer anzahlBeschwerde;
	private Integer anzahlVerfuegungen;
	private Integer anzahlVerfuegungenNormal;
	private Integer anzahlVerfuegungenMaxEinkommen;
	private Integer anzahlVerfuegungenKeinPensum;
	private Integer anzahlVerfuegungenZuschlagZumPensum;
	private Integer anzahlVerfuegungenNichtEintreten;
	private Integer anzahlSteueramtAusgeloest;
	private Integer anzahlSteueramtGeprueft;

	public GesuchZeitraumDataRow(String bgNummer, Integer gesuchLaufNr, String institution, String betreuungsTyp,
		String periode, Integer anzahlGesuchOnline, Integer anzahlGesuchPapier, Integer anzahlMutationOnline,
		Integer anzahlMutationPapier, Integer anzahlMutationAbwesenheit, Integer anzahlMutationBetreuung,
		Integer anzahlMutationEV, Integer anzahlMutationEwerbspensum, Integer anzahlMutationFamilienSitutation,
		Integer anzahlMutationFinanzielleSituation, Integer anzahlMutationGesuchsteller, Integer anzahlMutationKinder,
		Integer anzahlMutationUmzug, Integer anzahlMahnungen, Integer anzahlSteueramtAusgeloest,
		Integer anzahlSteueramtGeprueft, Integer anzahlBeschwerde, Integer anzahlVerfuegungen,
		Integer anzahlVerfuegungenNormal, Integer anzahlVerfuegungenMaxEinkommen, Integer anzahlVerfuegungenKeinPensum,
		Integer anzahlVerfuegungenZuschlagZumPensum, Integer anzahlVerfuegungenNichtEintreten) {
		this.bgNummer = bgNummer;
		this.gesuchLaufNr = gesuchLaufNr;
		this.institution = institution;
		this.betreuungsTyp = betreuungsTyp;
		this.periode = periode;
		this.anzahlGesuchOnline = anzahlGesuchOnline;
		this.anzahlGesuchPapier = anzahlGesuchPapier;
		this.anzahlMutationOnline = anzahlMutationOnline;
		this.anzahlMutationPapier = anzahlMutationPapier;
		this.anzahlMutationAbwesenheit = anzahlMutationAbwesenheit;
		this.anzahlMutationBetreuung = anzahlMutationBetreuung;
		this.anzahlMutationEV = anzahlMutationEV;
		this.anzahlMutationEwerbspensum = anzahlMutationEwerbspensum;
		this.anzahlMutationFamilienSitutation = anzahlMutationFamilienSitutation;
		this.anzahlMutationFinanzielleSituation = anzahlMutationFinanzielleSituation;
		this.anzahlMutationGesuchsteller = anzahlMutationGesuchsteller;
		this.anzahlMutationKinder = anzahlMutationKinder;
		this.anzahlMutationUmzug = anzahlMutationUmzug;
		this.anzahlMahnungen = anzahlMahnungen;
		this.anzahlBeschwerde = anzahlBeschwerde;
		this.anzahlVerfuegungen = anzahlVerfuegungen;
		this.anzahlVerfuegungenNormal = anzahlVerfuegungenNormal;
		this.anzahlVerfuegungenMaxEinkommen = anzahlVerfuegungenMaxEinkommen;
		this.anzahlVerfuegungenKeinPensum = anzahlVerfuegungenKeinPensum;
		this.anzahlVerfuegungenZuschlagZumPensum = anzahlVerfuegungenZuschlagZumPensum;
		this.anzahlVerfuegungenNichtEintreten = anzahlVerfuegungenNichtEintreten;
		this.anzahlSteueramtAusgeloest = anzahlSteueramtAusgeloest;
		this.anzahlSteueramtGeprueft = anzahlSteueramtGeprueft;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public String getBgNummer() {
		return bgNummer;
	}

	public void setBgNummer(String bgNummer) {
		this.bgNummer = bgNummer;
	}

	public Integer getGesuchLaufNr() {
		return gesuchLaufNr;
	}

	public void setGesuchLaufNr(Integer gesuchLaufNr) {
		this.gesuchLaufNr = gesuchLaufNr;
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

	public Integer getAnzahlSteueramtAusgeloest() {
		return anzahlSteueramtAusgeloest;
	}

	public void setAnzahlSteueramtAusgeloest(Integer anzahlSteueramtAusgeloest) {
		this.anzahlSteueramtAusgeloest = anzahlSteueramtAusgeloest;
	}

	public Integer getAnzahlSteueramtGeprueft() {
		return anzahlSteueramtGeprueft;
	}

	public void setAnzahlSteueramtGeprueft(Integer anzahlSteueramtGeprueft) {
		this.anzahlSteueramtGeprueft = anzahlSteueramtGeprueft;
	}
}
