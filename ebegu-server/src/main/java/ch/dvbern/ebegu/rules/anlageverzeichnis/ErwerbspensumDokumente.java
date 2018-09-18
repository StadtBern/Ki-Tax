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

import java.time.LocalDate;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.enums.DokumentGrundPersonType;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.enums.Taetigkeit;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;

/**
 * Dokumente für Erwerbspensum:
 * <p>
 * Arbeitsvertrag / Stundennachweise / sonstiger Nachweis über Erwerbspensum:
 * Wird nur bei Mutation des Erwerbspensums Angestellt verlangt oder bei Neueintritt im Job. Neueintritt = DatumVon >= Periodenstart
 * <p>
 * Nachweis Selbständigkeit oder AHV-Bestätigung:
 * z.B. für Künstler, müssen Projekte belegen
 * Notwendig, wenn ein Pensum für Selbständigkeit erfasst wurde
 * <p>
 * Nachweis über Ausbildung (z.B. Ausbildungsvertrag, Immatrikulationsbestätigung):
 * Notwendig, wenn ein Pensum für Ausbildung erfasst wurde
 * <p>
 * RAV-Bestätigung oder Nachweis der Vermittelbarkeit:
 * Notwendig, wenn ein Pensum für RAV erfasst wurde
 * <p>
 * Bestätigung (ärztliche Indikation):
 * Notwendig, wenn Frage nach GS Gesundheitliche Einschränkung mit Ja beantwortet wird (gesundheitliche Einschränkung)
 * <p>
 * <p>
 * Dokumente für Erwerbspensumzuschlag:
 * <p>
 * Nachweis über die unregelmässige Arbeitszeit (z.B. ArbG-Bestätigung):
 * Wenn Zuschlag zum Erwerbspensum mit Ja beantwortet und als Grund „Unregelmässige Arbeitszeit“ ausgewählt
 * <p>
 * Nachweis über langen Arbeitsweg:
 * Wenn Zuschlag zum Erwerbspensum mit Ja beantwortet und als Grund „Langer Arbeitsweg“ ausgewählt
 * <p>
 * Grund für sonstigen Zuschlag (z.B. Tod) …. Bessere Formulierung folg:
 * Wenn Zuschlag zum Erwerbspensum mit Ja beantwortet und als Grund „Andere“ ausgewählt
 * Wird nur von JA hochgeladen
 * <p>
 * Gleiche Arbeitstage bei Teilzeit:
 * Wenn Zuschlag zum Erwerbspensum mit Ja beantwortet und als Grund „Überlappende Arbeitszeiten“ ausgewählt
 * <p>
 * Fixe Arbeitszeiten:
 * Wenn Zuschlag zum Erwerbspensum mit Ja beantwortet und als Grund „Fixe Arbeitszeiten“ ausgewählt
 **/
public class ErwerbspensumDokumente extends AbstractDokumente<Erwerbspensum, LocalDate> {

	@Override
	public void getAllDokumente(@Nonnull Gesuch gesuch, @Nonnull Set<DokumentGrund> anlageVerzeichnis) {

		final LocalDate gueltigAb = gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb();

		final GesuchstellerContainer gesuchsteller1 = gesuch.getGesuchsteller1();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1, 1, gueltigAb);

		final GesuchstellerContainer gesuchsteller2 = gesuch.getGesuchsteller2();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2, 2, gueltigAb);
	}

	private void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, GesuchstellerContainer gesuchsteller, Integer gesuchstellerNumber, LocalDate gueltigAb) {
		if (gesuchsteller == null || gesuchsteller.getErwerbspensenContainers().isEmpty()) {
			return;
		}

		final Set<ErwerbspensumContainer> erwerbspensenContainers = gesuchsteller.getErwerbspensenContainers();

		for (ErwerbspensumContainer erwerbspensenContainer : erwerbspensenContainers) {
			final Erwerbspensum erwerbspensumJA = erwerbspensenContainer.getErwerbspensumJA();
			if (erwerbspensumJA != null) {
				add(getDokument(DokumentTyp.NACHWEIS_ERWERBSPENSUM, erwerbspensumJA, gueltigAb, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,

					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_RAV, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);

				add(getDokument(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
				add(getDokument(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensumJA, erwerbspensumJA.getName(), DokumentGrundPersonType.GESUCHSTELLER,
					gesuchstellerNumber, DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			}
		}
	}

	@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
	@Override
	public boolean isDokumentNeeded(@Nonnull DokumentTyp dokumentTyp, Erwerbspensum erwerbspensum, LocalDate periodenstart) {
		if (erwerbspensum != null) {
			switch (dokumentTyp) {
			case NACHWEIS_ERWERBSPENSUM:
				// Wird nur bei Neueintritt im Job verlangt. Neueintritt = DatumVon >= Periodenstart. Bei Mutationen
				// wird das Erwerbspensum immer beendet und ein neues erfasst. Daher gilt diese Regel immer
				return !erwerbspensum.getGueltigkeit().getGueltigAb().isBefore(periodenstart) &&
					erwerbspensum.getTaetigkeit() == Taetigkeit.ANGESTELLT;
			default:
				return isDokumentNeeded(dokumentTyp, erwerbspensum);
			}
		}
		return false;
	}

	@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
	@Override
	public boolean isDokumentNeeded(@Nonnull DokumentTyp dokumentTyp, @Nullable Erwerbspensum erwerbspensum) {
		if (erwerbspensum != null) {
			switch (dokumentTyp) {
			case NACHWEIS_ERWERBSPENSUM:
				// braucht Periodenstart-Datum als Parameter
				return false;
			case NACHWEIS_SELBSTAENDIGKEIT:
				return erwerbspensum.getTaetigkeit() == Taetigkeit.SELBSTAENDIG;
			case NACHWEIS_AUSBILDUNG:
				return erwerbspensum.getTaetigkeit() == Taetigkeit.AUSBILDUNG;
			case NACHWEIS_RAV:
				return erwerbspensum.getTaetigkeit() == Taetigkeit.RAV;
			case BESTAETIGUNG_ARZT:
				return erwerbspensum.getTaetigkeit() == Taetigkeit.GESUNDHEITLICHE_EINSCHRAENKUNGEN;
			case NACHWEIS_UNREG_ARBEITSZ:
				return erwerbspensum.getZuschlagZuErwerbspensum()
					&& erwerbspensum.getZuschlagsgrund() == Zuschlagsgrund.UNREGELMAESSIGE_ARBEITSZEITEN;
			case NACHWEIS_LANG_ARBEITSWEG:
				return erwerbspensum.getZuschlagZuErwerbspensum()
					&& erwerbspensum.getZuschlagsgrund() == Zuschlagsgrund.LANGER_ARBWEITSWEG;
			case NACHWEIS_SONSTIGEN_ZUSCHLAG:
				return erwerbspensum.getZuschlagZuErwerbspensum()
					&& erwerbspensum.getZuschlagsgrund() == Zuschlagsgrund.ANDERE;
			case NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT:
				return erwerbspensum.getZuschlagZuErwerbspensum()
					&& erwerbspensum.getZuschlagsgrund() == Zuschlagsgrund.UEBERLAPPENDE_ARBEITSZEITEN;
			case NACHWEIS_FIXE_ARBEITSZEITEN:
				return erwerbspensum.getZuschlagZuErwerbspensum()
					&& erwerbspensum.getZuschlagsgrund() == Zuschlagsgrund.FIXE_ARBEITSZEITEN;
			default:
				return false;
			}
		}
		return false;
	}

}
