package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;


/**
 * Abstrakte Klasse zum berechnen der benötigten Dokumente
 *
 * @param <T>
 */
abstract class AbstractDokumente<T> {

	abstract public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis);

	abstract public boolean isDokumentNeeded(DokumentTyp dokumentTyp, T dataForDocument);


	void add(DokumentGrund dokumentGrund, Set<DokumentGrund> anlageVerzeichnis) {
		if (dokumentGrund != null) {
			anlageVerzeichnis.add(dokumentGrund);
		}
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T dataForDocument, String fullName, String tag, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, dataForDocument)) {
			return new DokumentGrund(dokumentGrundTyp, fullName, tag, dokumentTyp);
		}
		return null;
	}
}
