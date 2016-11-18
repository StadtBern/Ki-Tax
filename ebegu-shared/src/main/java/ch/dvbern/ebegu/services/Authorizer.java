package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;

import java.util.List;

/**
 * Interface fuer eine Klasse welche prueft ob der aktuelle Benutzer fuer ein Gesuch berechtigt ist
 * Wirft eine Exception wenn der aktuelle Benutzer nicht berechtigt ist.
 */
public interface Authorizer {

	void checkReadAuthorization(Gesuch gesuch);

	void checkReadAuthorizationGesuchId(String gesuchId);

	/**
	 * prueft ob der aktuell eingeloggte benutzer das gesuch schreiben darf
	 */
	void checkWriteAuthorization(Gesuch gesuch);

	/**
	 * prueft ob der aktuelle user berechtigt ist ein gesuch zu erstellen
	 */
	void checkGesuchCreateAuth();

	/**
	 * prueft ob ein Benutzer einen Fall lesen kall
	 */
	void checkReadAuthorizationFall(String fallId);

	/**
	 * prueft ob der aktuell eingeloggte benutzer den Fall mit id schreibend bearbeiten darf
	 */
	void checkWriteAuthorizationFall(String fallId);

	/**
	 * prueft ob der aktuell eingeloggte benutzer den fall lesen darf
	 */
	void checkReadAuthorizationFall(Fall fall);

	/**
	 * prueft ob der aktuell eingeloggte benutzer die betreuung lesen darf
	 */
	void checkReadAuthorization(Betreuung betr);

	/**
	 * prueft ob der aktuell eingeloggte benutzer die betreuung schreibend bearbeiten darf
	 */
	void checkWriteAuthorization(Betreuung betreuungToRemove);


	/**
	 * prueft ob der aktuell eingeloggte benutzer ALLE betreuung in der Liste lesen darf
	 */
	void checkReadAuthorization(List<Betreuung> betreuungen);
}
