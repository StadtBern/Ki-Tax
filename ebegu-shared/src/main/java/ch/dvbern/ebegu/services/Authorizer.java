package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface fuer eine Klasse welche prueft ob der aktuelle Benutzer fuer ein Gesuch berechtigt ist
 * Wirft eine Exception wenn der aktuelle Benutzer nicht berechtigt ist.
 */
public interface Authorizer {

	void checkReadAuthorization(@Nullable  Gesuch gesuch);

	void checkReadAuthorizationGesuche(@Nullable  Collection<Gesuch> gesuche);

	void checkReadAuthorizationGesuchId(String gesuchId);

	/**
	 * prueft ob der aktuell eingeloggte benutzer das gesuch schreiben darf
	 */
	void checkWriteAuthorization(@Nullable  Gesuch gesuch);

	/**
	 * prueft ob der aktuelle user berechtigt ist ein gesuch zu erstellen
	 */
	void checkCreateAuthorizationGesuch();

	/**
	 * prueft ob ein Benutzer einen Fall lesen kall
	 */
	void checkReadAuthorizationFall(String fallId);

	/**
	 * prueft ob der aktuell eingeloggte benutzer den Fall mit id schreibend bearbeiten darf
	 */
	void checkWriteAuthorization(@Nullable Fall fall);


	/**
	 * prueft ob der aktuell eingeloggte benutzer den fall lesen darf
	 */
	void checkReadAuthorizationFall(@Nullable Fall fall);


	/**
	 * prueft ob der aktuell eingeloggte benutzer fuer ALLE uebergebnen faelle berechtigt ist
	 * @param faelle
	 */
	void checkReadAuthorizationFaelle(@Nullable  Collection<Fall> faelle);

	/**
	 * prueft ob der aktuell eingeloggte benutzer die betreuung lesen darf
	 */
	void checkReadAuthorization(@Nullable  Betreuung betr);


	/**
	 * prueft ob der aktuell eingeloggte benutzer die betreuung schreibend bearbeiten darf
	 */
	void checkWriteAuthorization(@Nullable  Betreuung betreuungToRemove);

	/**
	 * prueft ob der aktuell eingeloggte benutzer ALLE betreuung in der Liste lesen darf
	 */
	void checkReadAuthorizationForAllBetreuungen(@Nullable Collection<Betreuung> betreuungen);


	/**
	 * prueft ob der  eingeloggte benutzer EINE der  betreuung in der Liste lesen darf
	 */
	void checkReadAuthorizationForAnyBetreuungen(@Nullable Collection<Betreuung> betreuungen);

	/**
	 * prueft ob der aktuell eingeloggte Benutzer die Verfuegung lesen darf
	 */
	void checkReadAuthorization(@Nullable  Verfuegung verfuegung);

	/**
	 * prueft ob der aktuell eingeloggte Benutzer die ALLE verfuegungen in der liste lesen darf
	 */
	void checkReadAuthorizationVerfuegungen(@Nullable  Collection<Verfuegung> verfuegungen);

	/**
	 * prueft ob der aktuell eingeloggte benutzer die verfuegung schreibend bearbeiten darf
	 */
	void checkWriteAuthorization(@Nullable  Verfuegung verfuegung);

	void checkReadAuthorization(@Nullable FinanzielleSituationContainer finanzielleSituation);


	void checkReadAuthorization(@Nonnull Collection<FinanzielleSituationContainer> finanzielleSituationen);

	void checkWriteAuthorization(@Nullable  FinanzielleSituationContainer finanzielleSituation);

	void checkCreateAuthorizationFinSit(@Nonnull FinanzielleSituationContainer finanzielleSituation);

	void checkReadAuthorization(@Nullable ErwerbspensumContainer ewpCnt);

	/**
	 * extrahiert die {@link FinanzielleSituation} von beiden GS wenn vorhanden und  prueft ob der aktuelle Benutzer berechtigt ist
	 */
	void checkReadAuthorizationFinSit(@Nullable Gesuch gesuch);

	void checkReadAuthorization(@Nullable WizardStep step);
}
