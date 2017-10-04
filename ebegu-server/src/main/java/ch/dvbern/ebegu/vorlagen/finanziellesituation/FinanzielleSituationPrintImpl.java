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

package ch.dvbern.ebegu.vorlagen.finanziellesituation;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 22.08.2016
*/

/**
 * DTO fuer den den FinanzielleSituation Print. Ergenzt FinanzDatenPrintImpl um einige Funktionen
 */
public class FinanzielleSituationPrintImpl extends FinanzDatenPrintImpl implements FinanzielleSituationPrint {

	private final String finanzielleSituationJahr;
	private final String fallNummer;

	/**
	 * Konstruktor
	 * @param fsGesuchsteller1
	 * @param fsGesuchsteller2
	 * @param finanzielleSituationJahr
	 * @param fallNummer
	 */
	public FinanzielleSituationPrintImpl(FinanzSituationPrintGesuchsteller fsGesuchsteller1,
										 FinanzSituationPrintGesuchsteller fsGesuchsteller2, String finanzielleSituationJahr,
										 String fallNummer) {

		super(fsGesuchsteller1, fsGesuchsteller2);
		this.finanzielleSituationJahr = finanzielleSituationJahr;
		this.fallNummer = fallNummer;

	}

	@Override
	public String getFallNummer() {

		return fallNummer;
	}

	@Override
	public String getFinanzielleSituationJahr() {

		return finanzielleSituationJahr;
	}
}
