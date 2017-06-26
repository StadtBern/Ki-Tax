package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Gesuch;

import javax.annotation.Nullable;

/**
 * Interface fuer eine Klasse welche prueft ob der aktuelle Benutzer fuer ein Gesuch berechtigt ist und darauf ein boolean
 * zurueckgibt. Wenn eine Exception geworfen werden soll, sollte das {@link Authorizer} interface verwendet werden
 */
public interface BooleanAuthorizer {

	boolean hasReadAuthorization(@Nullable Gesuch gesuch);

}
