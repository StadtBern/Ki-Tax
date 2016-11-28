package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.enums.DokumentTyp;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Allgemeine Utils fuer Dokumente
 */
public class DokumenteUtil {

	/**
	 * Zusammenfügen der benötigten Dokument-Gruende (Dokumente die gem. den Angeben des GS gebraucht werden  und der
	 * Dokument-Gruende auf der DB (vorhandene Dokumente). Das entspricht allso einer Union der beiden Sets
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

	/**
	 * Fuer den gegebenen GeneratedDokumentTyp gibt die Methode den entsprechenden Dateinamen zurueck.
	 * @param typ
	 * @return
	 */
	@Nonnull
	public static String getFileNameForGeneratedDokumentTyp(final GeneratedDokumentTyp typ, final String identificationNumber) {
		switch (typ) {
			case BEGLEITSCHREIBEN: return ServerMessageUtil.translateEnumValue(GeneratedDokumentTyp.BEGLEITSCHREIBEN, identificationNumber);
			case FINANZIELLE_SITUATION: return ServerMessageUtil.translateEnumValue(GeneratedDokumentTyp.FINANZIELLE_SITUATION, identificationNumber);
			case VERFUEGUNG: return ServerMessageUtil.translateEnumValue(GeneratedDokumentTyp.VERFUEGUNG, identificationNumber);
			case MAHNUNG: return ServerMessageUtil.translateEnumValue(GeneratedDokumentTyp.MAHNUNG, identificationNumber);
			default: return "file.pdf";
		}
	}
}
