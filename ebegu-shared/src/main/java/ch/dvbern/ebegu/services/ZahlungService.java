package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * Service zum Verwalten von Zahlungen
 */
public interface ZahlungService {


	/**
	 * Ermittelt alle im aktuellen Monat gueltigen Verfuegungen, sowie aller seit dem letzten Auftrag eingeganegenen
	 * Mutationen.
	 * Der Zahlungsauftrag hat den initialen Status ENTWURF
	 * Als datumGeneriert wird "Jetzt" verwendet
	 */
	Zahlungsauftrag zahlungsauftragErstellen(LocalDate datumFaelligkeit, String beschreibung);

	/**
	 * Aktualisiert das Fälligkeitsdatum und die Beschreibung im übergebenen Auftrag. Die Zahlungspositionen werden
	 * *nicht* neu generiert
	 */
	Zahlungsauftrag zahlungsauftragAktualisieren(String auftragId, LocalDate datumFaelligkeit, String beschreibung);

	/**
	 * Ermittelt alle im aktuellen Monat gueltigen Verfuegungen, sowie aller seit dem letzten Auftrag eingeganegenen
	 * Mutationen.
	 * Der Zahlungsauftrag hat den initialen Status ENTWURF
	 */
	Zahlungsauftrag zahlungsauftragErstellen(LocalDate datumFaelligkeit, String beschreibung, LocalDateTime datumGeneriert);

	/**
	 * Nachdem alle Daten kontrolliert wurden, wird der Zahlungsauftrag ausgeloest. Danach kann er nicht mehr
	 * geloescht werden
	 */
	Zahlungsauftrag zahlungsauftragAusloesen(String auftragId);

	/**
	 * Sucht einen einzelnen Zahlungsauftrag.
	 */
	Optional<Zahlungsauftrag> findZahlungsauftrag(String auftragId);

	/**
	 * Gibt die Zahlung mit der uebergebenen Id zurueck.
	 */
	Optional<Zahlung> findZahlung(String zahlungId);

	/**
	 * Loescht einen Zahlungsauftrag (nur im Status ENTWURF moeglich)
	 */
	void deleteZahlungsauftrag(String auftragId);

	/**
	 * Gibt alle Zahlungsauftraege zurueck
	 */
	Collection<Zahlungsauftrag> getAllZahlungsauftraege();

	/**
	 * Eine Kita kann/muss den Zahlungseingang bestaetigen
	 */
	Zahlung zahlungBestaetigen(String zahlungId);


	Collection<Zahlungsauftrag> getZahlungsauftraegeInPeriode(LocalDate von, @Nonnull LocalDate bis);

}
