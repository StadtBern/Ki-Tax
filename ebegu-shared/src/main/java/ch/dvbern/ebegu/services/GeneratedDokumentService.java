package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.GeneratedDokument;

import javax.annotation.Nonnull;

/**
 * Service zum Verwalten von GeneratedDokumenten
 */
public interface GeneratedDokumentService {

	/**
	 * Erstellt ein neues GeneratedDokument wenn es noch nicht existiert und sonst aktualisiert das Bestehende
	 * @param dokument
	 * @return
	 */
	@Nonnull
	GeneratedDokument saveGeneratedDokument(@Nonnull GeneratedDokument dokument);
}
