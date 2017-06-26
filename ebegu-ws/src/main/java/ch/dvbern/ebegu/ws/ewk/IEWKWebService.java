package ch.dvbern.ebegu.ws.ewk;

import ch.dvbern.ebegu.dto.personensuche.EWKResultat;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.errors.PersonenSucheServiceBusinessException;
import ch.dvbern.ebegu.errors.PersonenSucheServiceException;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Serviceinterface welches die Methoden des EWK Service zur verfuegung stellt
 */
public interface IEWKWebService {


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
	 * Sucht eine Person im EWK, anhand Nachname, Geburtsdatum und Geschlecht
	 */
	@Nonnull
	EWKResultat suchePerson(@Nonnull String name, @Nonnull LocalDate geburtsdatum, @Nonnull Geschlecht geschlecht) throws PersonenSucheServiceException, PersonenSucheServiceBusinessException;
}
