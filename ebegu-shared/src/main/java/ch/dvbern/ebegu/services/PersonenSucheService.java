package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.personensuche.EWKPerson;
import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Service für die Personensuche
 */
public interface PersonenSucheService {

	/**
	 * Sucht den uebergebenen Gesuchsteller im EWK.
	 * Falls die Suche eindeutig ist, wird die ewkPersonenId auf dem Gesuchsteller gesetzt
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull Gesuchsteller gesuchsteller) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Verknuepft die uebergebene EWK-Person mit dem Gesuchsteller: Setzt die ewkPersonenId auf
	 * dem Gesuchsteller
	 */
	@Nonnull
	Gesuchsteller selectPerson(@Nonnull Gesuchsteller gesuchsteller, @Nonnull String ewkPersonID);

	/**
	 * Sucht eine Person im EWK, anhand eindeutiger PersonenID
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String id) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Sucht eine Person im EWK, mit allen Angaben
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String name, @Nonnull String vorname, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;

	/**
	 * Sucht eine Person im EWK, mit allen Angaben ausser Vorname
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;
}


