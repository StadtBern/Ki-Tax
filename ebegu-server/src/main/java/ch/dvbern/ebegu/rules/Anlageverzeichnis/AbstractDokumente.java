package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;


/**
 * Abstrakte Klasse zum berechnen der benötigten Dokumente
 *
 * @param <T1>
 */
abstract class AbstractDokumente<T1, T2> {

	abstract public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis);

	abstract public boolean isDokumentNeeded(DokumentTyp dokumentTyp, T1 dataForDocument);

	public boolean isDokumentNeeded(DokumentTyp dokumentTyp, T1 dataForDocument1, T2 dataForDocument2) {
		return isDokumentNeeded(dokumentTyp, dataForDocument1);
	}

	void add(DokumentGrund dokumentGrund, Set<DokumentGrund> anlageVerzeichnis) {
		if (dokumentGrund != null) {
			anlageVerzeichnis.add(dokumentGrund);
		}
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T1 dataForDocument, String fullName, String tag, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, dataForDocument)) {
			return new DokumentGrund(dokumentGrundTyp, fullName, tag, dokumentTyp);
		}
		return null;
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T1 dataForDocument1, T2 dataForDocument2, String fullName, String tag, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, dataForDocument1, dataForDocument2)) {
			return new DokumentGrund(dokumentGrundTyp, fullName, tag, dokumentTyp);
		}
		return null;
	}
}
