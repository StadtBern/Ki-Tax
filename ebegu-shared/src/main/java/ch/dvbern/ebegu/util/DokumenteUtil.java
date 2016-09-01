package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.enums.DokumentTyp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Allgemeine Utils fuer Dokumente
 */
public class DokumenteUtil {

	/**
	 * Zusammenfügen der benötigten Dokument Gründe und der Dokument Gründe auf der DB (vorhandene Dokumente)
	 */
	public static Set<DokumentGrund> mergeNeededAndPersisted(Set<DokumentGrund> dokumentGrundsNeeded, Collection<DokumentGrund> persistedDokumentGrunds) {

		Set<DokumentGrund> dokumentGrundsMerged = new HashSet<>();
		Set<DokumentGrund> persistedDokumentAdded = new HashSet<>();

		// Ersetzen des Placeholder mit dem vorhandenen Dokument, falls schon ein Dokument gespeichert wurde...
		for (DokumentGrund dokumentGrundNeeded : dokumentGrundsNeeded) {
			Set<DokumentGrund> persistedForNeeded = getPersistedForNeeded(persistedDokumentGrunds, dokumentGrundNeeded);

			if (!persistedForNeeded.isEmpty()) {
				persistedDokumentAdded.addAll(persistedForNeeded);
				dokumentGrundsMerged.addAll(persistedForNeeded);
			} else {
				dokumentGrundsMerged.add(dokumentGrundNeeded);
			}
		}

		//Hinzufügen der vorhandenen Dokumente welche jedoch eigentlich nicht mehr benötigt werden.
		persistedDokumentGrunds.removeAll(persistedDokumentAdded);
		for (DokumentGrund persistedDokumentGrund : persistedDokumentGrunds) {
			persistedDokumentGrund.setNeeded(false);
			dokumentGrundsMerged.add(persistedDokumentGrund);
		}

		return dokumentGrundsMerged;

	}

	private static Set<DokumentGrund> getPersistedForNeeded(Collection<DokumentGrund> persistedDokumentGrunds, DokumentGrund dokumentGrundNeeded) {
		Set<DokumentGrund> persisted = new HashSet<>();
		for (DokumentGrund persistedDokumentGrund : persistedDokumentGrunds) {
			if (persistedDokumentGrund.getDokumentGrundTyp().equals(dokumentGrundNeeded.getDokumentGrundTyp())) {
				final DokumentTyp dokumentTypPersisted = persistedDokumentGrund.getDokumentTyp();
				final DokumentTyp dokumentTypNeeded = dokumentGrundNeeded.getDokumentTyp();
				if (dokumentTypNeeded.equals(dokumentTypPersisted)) {
					persisted.add(persistedDokumentGrund);
				}
			}
		}
		return persisted;
	}

}
