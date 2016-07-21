package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Set;


abstract class AbstractDokumente<T> {


	abstract public void getAllDokumente(Gesuch gesuch, Set<DokumentGrund> anlageVerzeichnis);
	abstract public boolean isDokumentNeeded(DokumentTyp dokumentTyp, T abstractFinanzielleSituation);


	void add(DokumentGrund dokumentGrund, Set<DokumentGrund> anlageVerzeichnis) {
		if(dokumentGrund != null){
			anlageVerzeichnis.add(dokumentGrund);
		}
	}

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T abstractFinanzielleSituation, String fullname, String tag, DokumentGrundTyp dokumentGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, abstractFinanzielleSituation)) {
			return new DokumentGrund(dokumentGrundTyp, fullname, tag, dokumentTyp);
		}
		return null;
	}
}
