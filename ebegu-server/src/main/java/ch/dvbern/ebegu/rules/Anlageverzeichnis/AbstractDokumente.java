package ch.dvbern.ebegu.rules.Anlageverzeichnis;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AnlageGrundTyp;
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

	DokumentGrund getDokument(DokumentTyp dokumentTyp, T abstractFinanzielleSituation, String tag1, String tag2, AnlageGrundTyp anlageGrundTyp) {
		if (isDokumentNeeded(dokumentTyp, abstractFinanzielleSituation)) {
			if (tag1 != null) {
				if (tag2 != null) {
					return new DokumentGrund(anlageGrundTyp, tag1, tag2, dokumentTyp);
				} else {
					return new DokumentGrund(anlageGrundTyp, tag1, dokumentTyp);
				}
			} else {
				return new DokumentGrund(anlageGrundTyp, dokumentTyp);
			}
		}
		return null;
	}
}
