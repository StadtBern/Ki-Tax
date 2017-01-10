package ch.dvbern.ebegu.rules.anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.enums.Kinderabzug;

import java.util.Set;

/**
 * Dokumente für Kinder:
 * <p>
 * Fachstellenbestätigung (soziale Indikation):
 * Notwendig, wenn Frage nach Kind Fachstelle involviert mit Ja beantwortet, und es wird nicht die „Fachstelle für
 * Behinderung“ ausgewählt (Eine bestimmte Fachstelle ist für die Bestätigung von Behinderungen zuständig)
 * Es ist entweder dieses Dokument oder die Fachstellenbestätigung Behinderung gefordert, aber nie beide
 * <p>
 * Fachstellenbestätigung (Behinderung):
 * Notwendig, wenn Frage nach Kind Fachstelle involviert mit Ja beantwortet, und es wird die „Fachstelle für Behinderung“
 * ausgewählt (Eine bestimmte Fachstelle ist für die Bestätigung von Behinderungen zuständig)
 * Es ist entweder dieses Dokument oder die Fachstellenbestätigung Soziale Indikation gefordert, aber nie beide
 **/
public class KindDokumente extends AbstractDokumente<Kind, Object> {

	@Override
	public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis) {

		final Set<KindContainer> kindContainers = gesuch.getKindContainers();
		if (kindContainers == null || kindContainers.isEmpty()) {
			return;
		}

		for (KindContainer kindContainer : kindContainers) {
			final Kind kindJA = kindContainer.getKindJA();

			add(getDokument(DokumentTyp.FACHSTELLENBEST_SOZ, kindJA, kindJA.getFullName(), null, DokumentGrundTyp.KINDER), anlageVerzeichnis);
			add(getDokument(DokumentTyp.FACHSTELLENBEST_BEH, kindJA, kindJA.getFullName(), null, DokumentGrundTyp.KINDER), anlageVerzeichnis);

		}
	}

	@Override
	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, Kind kind) {
		if (kind != null) {
			switch (dokumentTyp) {
				case FACHSTELLENBEST_SOZ:
					return kind.getPensumFachstelle() != null && kind.getPensumFachstelle().getFachstelle() != null
						&& !kind.getPensumFachstelle().getFachstelle().isBehinderungsbestaetigung();
				case FACHSTELLENBEST_BEH:
					return kind.getPensumFachstelle() != null && kind.getPensumFachstelle().getFachstelle() != null
						&& kind.getPensumFachstelle().getFachstelle().isBehinderungsbestaetigung();
				default:
					return false;
			}
		}
		return false;
	}

}
