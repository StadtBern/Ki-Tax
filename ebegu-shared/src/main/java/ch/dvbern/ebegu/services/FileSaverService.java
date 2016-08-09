package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Institution;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Institutionen
 */
public interface FileSaverService {

	String save(byte[] file, String fileName, String GesuchId);


}
