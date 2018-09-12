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

package ch.dvbern.ebegu.rules.anlageverzeichnis;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.AbstractFinanzielleSituation;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

/**
 * Dokumente für Einkommensverschlechterung:
 * <p>
 * Unterlagen zur aktuellen Einkommenssituation (pro Monat)
 * Notwendig, wenn Monatseinkünfte > 0
 * <p>
 * Nachweis über Familienzulagen (soweit nicht im Nettolohn enthalten)
 * Notwendig, wenn Familienzulage > 0
 * <p>
 * Nachweis über Ersatzeinkommen
 * Notwendig, wenn Ersatzeinkommen > 0
 * <p>
 * Nachweis über erhaltene Alimente (Unterhaltsbeiträge)
 * Notwendig, wenn erhaltene Alimente > 0
 * <p>
 * Nachweis über geleistete Alimente
 * Notwendig, wenn geleistete Alimente > 0
 * <p>
 * Nachweis über das Vermögen, Stand 31.12. (z.B.: Kto.-Auszug, Immobilien usw.)
 * Notwendig, wenn Vermögen > 0
 * <p>
 * Nachweis über die Schulden, Stand: 31.12. (z.B.: Kto.-Auszug, Darlehensvertrag usw.)
 * Notwendig, wenn Schulden > 0
 * <p>
 * Erfolgsrechnungen der letzten drei Jahre (Jahr der Einkommensverschlechterung: x, x-1, x-2)
 * Notwendig, wenn Erfolgsrechnungen des Jahres nicht null
 **/
public class EinkommensverschlechterungDokumente extends AbstractFinanzielleSituationDokumente {

	@Override
	public void getAllDokumente(@Nonnull Gesuch gesuch, @Nonnull Set<DokumentGrund> anlageVerzeichnis) {

		final boolean gemeinsam = gesuch.extractFamiliensituation() != null &&
			gesuch.extractFamiliensituation().getGemeinsameSteuererklaerung() != null &&
			gesuch.extractFamiliensituation().getGemeinsameSteuererklaerung();

		final EinkommensverschlechterungInfo einkommensverschlechterungInfo = gesuch.extractEinkommensverschlechterungInfo();

		final int basisJahrPlus1 = gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear().getYear() + 1;
		final int basisJahrPlus2 = gesuch.getGesuchsperiode().getGueltigkeit().calculateEndOfPreviousYear().getYear() + 2;

		final GesuchstellerContainer gesuchsteller1 = gesuch.getGesuchsteller1();
		final GesuchstellerContainer gesuchsteller2 = gesuch.getGesuchsteller2();

		if (einkommensverschlechterungInfo != null) {
			if (einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1()) {
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, gemeinsam, 1, 1, basisJahrPlus1);
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, gemeinsam, 2, 1, basisJahrPlus1);
			}
			if (einkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2()) {
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, gemeinsam, 1, 2, basisJahrPlus2);
				getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, gemeinsam, 2, 2, basisJahrPlus2);
			}
		}

	}

	private void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, GesuchstellerContainer gesuchsteller,
		boolean gemeinsam, int gesuchstellerNumber, int basisJahrPlusNumber, int basisJahr) {

		if (gesuchsteller == null || gesuchsteller.getEinkommensverschlechterungContainer() == null) {
			return;
		}

		final EinkommensverschlechterungContainer einkommensverschlechterungContainer = gesuchsteller.getEinkommensverschlechterungContainer();
		Einkommensverschlechterung einkommensverschlechterung;
		if (basisJahrPlusNumber == 2) {
			einkommensverschlechterung = einkommensverschlechterungContainer.getEkvJABasisJahrPlus2();
		} else {
			einkommensverschlechterung = einkommensverschlechterungContainer.getEkvJABasisJahrPlus1();
		}

		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller.extractFullName(), basisJahr, gemeinsam,
			gesuchstellerNumber, einkommensverschlechterung, DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);

		add(getDokument(DokumentTyp.NACHWEIS_EINKOMMENSSITUATION_MONAT, einkommensverschlechterung, gesuchsteller.extractFullName(),
			String.valueOf(basisJahr), DokumentGrundPersonType.GESUCHSTELLER,
			gesuchstellerNumber, DokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG), anlageVerzeichnis);

	}

	@Override
	protected boolean isMonatsLohnausweisNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation) {
		if (abstractFinanzielleSituation instanceof Einkommensverschlechterung) {

			Einkommensverschlechterung einkommensverschlechterung = (Einkommensverschlechterung) abstractFinanzielleSituation;

			return !einkommensverschlechterung.getSteuerveranlagungErhalten() &&
				(
					einkommensverschlechterung.getNettolohnJan() != null && einkommensverschlechterung.getNettolohnJan().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnFeb() != null && einkommensverschlechterung.getNettolohnFeb().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnMrz() != null && einkommensverschlechterung.getNettolohnMrz().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnApr() != null && einkommensverschlechterung.getNettolohnApr().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnMai() != null && einkommensverschlechterung.getNettolohnMai().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnJun() != null && einkommensverschlechterung.getNettolohnJun().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnJul() != null && einkommensverschlechterung.getNettolohnJul().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnAug() != null && einkommensverschlechterung.getNettolohnAug().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnSep() != null && einkommensverschlechterung.getNettolohnSep().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnOkt() != null && einkommensverschlechterung.getNettolohnOkt().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnNov() != null && einkommensverschlechterung.getNettolohnNov().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnDez() != null && einkommensverschlechterung.getNettolohnDez().compareTo(BigDecimal.ZERO) > 0 ||
						einkommensverschlechterung.getNettolohnZus() != null && einkommensverschlechterung.getNettolohnZus().compareTo(BigDecimal.ZERO) > 0
				);
		}
		return false;
	}

	@Override
	protected boolean isErfolgsrechnungNeeded(AbstractFinanzielleSituation abstractFinanzielleSituation, int minus) {
		if (abstractFinanzielleSituation instanceof Einkommensverschlechterung) {
			Einkommensverschlechterung einkommensverschlechterung = (Einkommensverschlechterung) abstractFinanzielleSituation;
			switch (minus) {
			case 0:
				return !einkommensverschlechterung.getSteuerveranlagungErhalten() && (einkommensverschlechterung.getGeschaeftsgewinnBasisjahr() != null);
			case 1:
				return !einkommensverschlechterung.getSteuerveranlagungErhalten() && (einkommensverschlechterung.getGeschaeftsgewinnBasisjahrMinus1() != null);
			}
		}
		return false;
	}

}
