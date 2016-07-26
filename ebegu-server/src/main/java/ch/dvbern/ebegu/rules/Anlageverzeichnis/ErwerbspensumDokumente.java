package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.enums.Taetigkeit;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;

import java.util.Set;

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
public class ErwerbspensumDokumente extends AbstractDokumente<Erwerbspensum> {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		final Gesuchsteller gesuchsteller1 = gesuch.getGesuchsteller1();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller1);

		final Gesuchsteller gesuchsteller2 = gesuch.getGesuchsteller2();
		getAllDokumenteGesuchsteller(anlageVerzeichnis, gesuchsteller2);
	}

	private void getAllDokumenteGesuchsteller(Set<DokumentGrund> anlageVerzeichnis, Gesuchsteller gesuchsteller) {
		if (gesuchsteller == null || gesuchsteller.getErwerbspensenContainers().isEmpty()) {
			return;
		}

		final Set<ErwerbspensumContainer> erwerbspensenContainers = gesuchsteller.getErwerbspensenContainers();

		for (ErwerbspensumContainer erwerbspensenContainer : erwerbspensenContainers) {
			final Erwerbspensum erwerbspensumJA = erwerbspensenContainer.getErwerbspensumJA();
			add(getDokument(DokumentTyp.NACHWEIS_ERWERBSPENSUM, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_SELBSTAENDIGKEIT, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_AUSBILDUNG, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_RAV, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.BESTAETIGUNG_ARZT, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);

			add(getDokument(DokumentTyp.NACHWEIS_UNREG_ARBEITSZ, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_LANG_ARBEITSWEG, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_SONSTIGEN_ZUSCHLAG, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_GLEICHE_ARBEITSTAGE_BEI_TEILZEIT, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
			add(getDokument(DokumentTyp.NACHWEIS_FIXE_ARBEITSZEITEN, erwerbspensumJA, gesuchsteller.getFullName(), erwerbspensumJA.getName(), DokumentGrundTyp.ERWERBSPENSUM), anlageVerzeichnis);
		}
	}


	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Erwerbspensum erwerbspensum) {
		if (erwerbspensum != null) {
			switch (dokumentTyp) {
				case NACHWEIS_ERWERBSPENSUM:
					// Todo: Wird nur bei Mutation des Erwerbspensums Angestellt verlangt oder bei Neueintritt im Job. Neueintritt = DatumVon >= Periodenstart
					// Mutation ist noch nicht implementiert...
					return false;
				case NACHWEIS_SELBSTAENDIGKEIT:
					return erwerbspensum.getTaetigkeit() == Taetigkeit.SELBSTAENDIG;
				case NACHWEIS_AUSBILDUNG:
					return erwerbspensum.getTaetigkeit() == Taetigkeit.AUSBILDUNG;
				case NACHWEIS_RAV:
					return erwerbspensum.getTaetigkeit() == Taetigkeit.RAV;
				case BESTAETIGUNG_ARZT:
					return erwerbspensum.getGesundheitlicheEinschraenkungen();
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
