/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.enums.AnmeldungMutationZustand;

/**
 * Service zum Verwalten von Betreuungen
 */
public interface BetreuungService {

	/**
	 * Speichert die Betreuung neu in der DB falls der Key noch nicht existiert. Sonst wird die existierende Betreuung aktualisiert
	 * Bean validation wird eingeschaltet
	 *
	 * @param betreuung Die Betreuung als DTO
	 */
	@Nonnull
	Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung, @Nonnull Boolean isAbwesenheit);

	/**
	 * Setzt die Betreuungsplatzanfrage auf ABGEWIESEN und sendet dem Gesuchsteller eine E-Mail
	 */
	@Nonnull
	Betreuung betreuungPlatzAbweisen(@Valid @Nonnull Betreuung betreuung);

	/**
	 * Setzt die Betreuungsplatzanfrage auf BESTAETIGT und sendet dem Gesuchsteller eine E-Mail,
	 * falls damit alle Betreuungen des Gesuchs bestaetigt sind.
	 */
	@Nonnull
	Betreuung betreuungPlatzBestaetigen(@Valid @Nonnull Betreuung betreuung);

	/**
	 * Setzt die Schulamt-Anmeldung auf SCHULAMT_ANMELDUNG_UEBERNOMMEN und sendet dem Gesuchsteller eine E-Mail.
	 */
	@Nonnull
	Betreuung anmeldungSchulamtUebernehmen(@Valid @Nonnull Betreuung betreuung);

	/**
	 * Setzt die Schulamt-Anmeldung auf SCHULAMT_ANMELDUNG_ABGELEHNT und sendet dem Gesuchsteller eine E-Mail
	 */
	@Nonnull
	Betreuung anmeldungSchulamtAblehnen(@Valid @Nonnull Betreuung betreuung);

	/**
	 * Setzt die Schulamt-Anmeldung auf SCHULAMT_FALSCHE_INSTITUTION.
	 */
	@Nonnull
	Betreuung anmeldungSchulamtFalscheInstitution(@Valid @Nonnull Betreuung betreuung);

	/**
	 * @param key PK (id) der Betreuung
	 * @return Betreuung mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Betreuung> findBetreuung(@Nonnull String key);

	/**
	 * @param key PK (id) der Betreuung
	 * @param doAuthCheck: Definiert, ob die Berechtigungen (Lesen/Schreiben) für diese Betreuung geprüft werden muss.
	 * @return Betreuung mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Betreuung> findBetreuung(@Nonnull String key, boolean doAuthCheck);

	/**
	 * @param bgNummer BGNummer der Betreuung
	 * @return Betreuung mit der angegebenen ID (z.B. 18.000116.1.2) oder null falls nicht vorhanden
	 */
	List<Betreuung> findBetreuungenByBGNummer(@Nonnull String bgNummer);

	/**
	 * @param bgNummer BGNummer der Betreuung
	 * @return neueste Betreuung mit der angegebenen ID (z.B. 18.000116.1.2) oder empty Optional falls nicht vorhanden
	 */
	Optional<Betreuung> findNewestBetreuungByBGNummer(@Nonnull String bgNummer);

	/**
	 * Extract Fallnummer form bgNummer
	 * @return Extracted Fallnummer
	 */
	Long getFallnummerFromBGNummer(String bgNummer);


	/**
	 * Extract Year form bgNummer
	 * @return Extracted year
	 */
	int getYearFromBGNummer(String bgNummer);

	/**
	 * Extract KindNummer form bgNummer
	 * @return Extracted kindNummer
	 */
	int getKindNummerFromBGNummer(String bgNummer);

	/**
	 * Extract BetreuungNummer form bgNummer
	 * @return Extracted BetreuungNummer
	 */
	int getBetreuungNummerFromBGNummer(String bgNummer);

	/**
	 * Validate bgNummer
	 */
	boolean validateBGNummer(String bgNummer);

	/**
	 * @param key PK (id) der Betreuung
	 * @return Betreuung mit dem gegebenen key inkl. Betreuungspensen oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Betreuung> findBetreuungWithBetreuungsPensen(@Nonnull String key);

	/**
	 * entfernt eine Betreuung aus der Databse
	 *
	 * @param betreuungId Id der Betreuung zu entfernen
	 */
	void removeBetreuung(@Nonnull String betreuungId);

	/**
	 * entfernt eine Betreuuung aus der Databse. Um diese Methode aufzurufen muss man sich vorher vergewissern, dass die Betreuuung existiert
	 */
	void removeBetreuung(@Nonnull Betreuung betreuung);

	/**
	 * Gibt die Pendenzen fuer einen Benutzer mit Rolle Institution oder Traegerschaft zurueck.
	 * Dies sind Betreuungen, welche zu einer Institution gehoeren, fuer welche der Benutzer berechtigt ist,
	 * und deren Status "WARTEN" ist.
	 */
	@Nonnull
	Collection<Betreuung> getPendenzenForInstitutionsOrTraegerschaftUser();

	@Nonnull
	List<Betreuung> findAllBetreuungenFromGesuch(String gesuchId);

	/**
	 * @param fall Fall, dessen verfuegte Betreuungen zurueckgegeben werden
	 * @return BetreuungList, welche zum Fall gehoeren oder null
	 */
	@Nonnull
	List<Betreuung> findAllBetreuungenWithVerfuegungFromFall(@Nonnull Fall fall);

	/**
	 * Schliesst die Betreuung (Status GESCHLOSSEN_OHNE_VERFUEGUNG) ohne eine neue Verfuegung zu erstellen
	 * (bei gleichbleibenden Daten)
	 */
	@Nonnull
	Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung);

	/**
	 * Gibt alle Betreuungen zurueck, welche Mutationen betreffen, die verfügt sind und deren
	 * betreuungMutiert-Flag noch nicht gesetzt sind
	 */
	@Nonnull
	List<Betreuung> getAllBetreuungenWithMissingStatistics();

	/**
	 * Gibt alle Abwesenheiten zurueck, welche Mutationen betreffen, die verfügt sind und deren
	 * abwesenheitMutiert-Flag noch nicht gesetzt sind
	 */
	@Nonnull
	List<Abwesenheit> getAllAbwesenheitenWithMissingStatistics();


	int changeAnmeldungMutationZustand(String betreuungsId, AnmeldungMutationZustand anmeldungMutationZustand);
}
