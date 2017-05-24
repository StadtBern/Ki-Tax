package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.dto.suchfilter.smarttable.AntragTableFilterDTO;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.enums.AntragStatus;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service zum Verwalten von Gesuche
 */
@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
public interface GesuchService {

	/**
	 * Erstellt ein neues Gesuch in der DB, falls der key noch nicht existiert
	 *
	 * @param gesuch der Gesuch als DTO
	 * @return das gespeicherte Gesuch
	 */
	@Nonnull
	Gesuch createGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Aktualisiert das Gesuch in der DB
	 *
	 * @param gesuch              das Gesuch als DTO
	 * @param saveInStatusHistory true wenn gewollt, dass die Aenderung in der Status gespeichert wird
	 * @return Das aktualisierte Gesuch
	 */
	@Nonnull
	Gesuch updateGesuch(@Nonnull Gesuch gesuch, boolean saveInStatusHistory);

	/**
	 * Laedt das Gesuch mit der id aus der DB. ACHTUNG zudem wird hier der Status auf IN_BEARBEITUNG_JA gesetzt
	 * wenn der Benutzer ein JA Mitarbeiter ist und das Gesuch in FREIGEGEBEN ist
	 * @param key PK (id) des Gesuches
	 * @return Gesuch mit dem gegebenen key oder null falls nicht vorhanden
	 */
	@Nonnull
	Optional<Gesuch> findGesuch(@Nonnull String key);

	/**
	 * Spezialmethode fuer die Freigabe. Kann Gesuche lesen die im Status Freigabequittung oder hoeher sind
	 */
	@Nonnull
	Optional<Gesuch> findGesuchForFreigabe(@Nonnull String gesuchId);

	/**
	 * Gibt alle Gesuche zurueck die in der Liste der gesuchIds auftauchen und fuer die der Benutzer berechtigt ist.
	 * Gesuche fuer die der Benutzer nicht berechtigt ist werden uebersprungen
	 * @param gesuchIds
	 *
	 */
	List<Gesuch> findReadableGesuche(@Nullable Collection<String> gesuchIds);

	/**
	 * Gibt alle existierenden Gesuche zurueck.
	 *
	 * @return Liste aller Gesuche aus der DB
	 */
	@Nonnull
	Collection<Gesuch> getAllGesuche();

	/**
	 * Gibt alle existierenden Gesuche zurueck, deren Status nicht VERFUEGT ist
	 * @return Liste aller Gesuche aus der DB
	 */
	@Nonnull
	Collection<Gesuch> getAllActiveGesuche();

	/**
	 * Gibt alle existierenden Gesuche zurueck, deren Status nicht VERFUEGT ist
	 * und die dem übergebenen Benutzer als "Verantwortliche Person" zugeteilt sind.
	 * @return Liste aller Gesuche aus der DB
	 */
	@Nonnull
	Collection<Gesuch> getAllActiveGesucheOfVerantwortlichePerson(@Nonnull String benutzername);

	/**
	 * entfernt ein Gesuch aus der Database
	 *
	 * @param gesuchId der Gesuch zu entfernen
	 */
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	void removeGesuch(@Nonnull String gesuchId);

	/**
	 * Gibt eine Liste von Gesuchen zureck, deren Gesuchsteller 1 den angegebenen Namen und Vornamen hat.
	 * Achtung, damit ist ein Gesuchsteller nicht eindeutig identifiziert!
	 */
	@Nonnull
	List<Gesuch> findGesuchByGSName(String nachname, String vorname);

	/**
	 * Gibt alle Antraege des aktuell eingeloggten Benutzers
     */
	@Nonnull
	List<Gesuch> getAntraegeByCurrentBenutzer();

	/**
	 * Methode welche jeweils eine bestimmte Menge an Suchresultate fuer die Paginatete Suchtabelle zuruckgibt,
	 *
	 * @param antragTableFilterDto
	 * @return Resultatpaar, der erste Wert im Paar ist die Anzahl Resultate, der zweite Wert ist die Resultatliste
	 */
	Pair<Long, List<Gesuch>> searchAntraege(AntragTableFilterDTO antragTableFilterDto);

	/**
	 * Gibt ein DTO mit saemtlichen Antragen eins bestimmten Falls zurueck
	 */
	@Nonnull
	List<JaxAntragDTO> getAllAntragDTOForFall(String fallId);

	/**
	 * Erstellt eine neue Mutation fuer die Gesuchsperiode und Fall des uebergebenen Antrags. Es wird immer der letzt
	 * verfuegte Antrag kopiert fuer die Mutation.
	 */
	@Nonnull
	Optional<Gesuch> antragMutieren(@Nonnull String antragId, @Nullable LocalDate eingangsdatum);

	/**
	 * hilfsmethode zur mutation von faellen ueber das gui. Wird fuer testzwecke benoetigt
	 */
	@Nonnull
	Optional<Gesuch> antragMutieren(@Nonnull Long fallNummer, @Nonnull String gesuchsperiodeId,
									@Nonnull LocalDate eingangsdatum);

	/**
	 * Erstellt ein Erneuerungsgesuch fuer die Gesuchsperiode und Fall des übergebenen Antrags. Es wird immer der
	 * letzte verfügte Antrag kopiert für das Erneuerungsgesuch
	 */
	@Nonnull
	Optional<Gesuch> antragErneuern(@Nonnull String antragId, @Nonnull String gesuchsperiodeId, @Nullable LocalDate eingangsdatum);

	/**
	 * Gibt das neueste Gesuch der im selben Fall und Periode wie das gegebene Gesuch ist.
	 * Es wird nach Erstellungsdatum geschaut
	 * @param gesuch
	 * @return
	 */
	@Nonnull
	Optional<Gesuch> getNeustesGesuchFuerGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Alle GesucheIDs des Gesuchstellers zurueckgeben fuer admin
	 */
	@Nonnull
	List<String> getAllGesuchIDsForFall(String fallId);

	/**
	 * Alle Gesuche fuer den gegebenen Fall in der gegebenen Periode
	 * @param fall
	 * @param gesuchsperiode
	 */
	@Nonnull
	List<Gesuch> getAllGesucheForFallAndPeriod(@Nonnull Fall fall, @Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Das gegebene Gesuch wird mit heutigem Datum freigegeben und den Step FREIGABE auf OK gesetzt
	 * @param gesuch
	 * @param statusToChangeTo
	 */
	Gesuch antragFreigabequittungErstellen(@Nonnull Gesuch gesuch, AntragStatus statusToChangeTo);

	/**
	 * Gibt das Gesuch frei für das Jugendamt: Anpassung des Status inkl Kopieren der Daten des GS aus den
	 * JA-Containern in die GS-Containern. Wird u.a. beim einlesen per Scanner aufgerufen
	 */
	@Nonnull
	Gesuch antragFreigeben(@Nonnull String gesuchId, @Nullable String username);

	/**
	 * Setzt das gegebene Gesuch als Beschwerde hängig und bei allen Gescuhen der Periode den Flag
	 * gesperrtWegenBeschwerde auf true.
	 * @return Gibt das aktualisierte gegebene Gesuch zurueck
	 */
	@Nonnull
	Gesuch setBeschwerdeHaengigForPeriode(@Nonnull Gesuch gesuch);

	/**
	 * Setzt das gegebene Gesuch als VERFUEGT und bei allen Gescuhen der Periode den Flag
	 * gesperrtWegenBeschwerde auf false
	 * @return Gibt das aktualisierte gegebene Gesuch zurueck
	 */
	@Nonnull
	Gesuch removeBeschwerdeHaengigForPeriode(@Nonnull Gesuch gesuch);

	/**
	 * Gibt die Antrags-Ids aller Antraege zurueck, welche im uebergebenen Zeitraum verfuegt wurden.
	 * Falls es mehrere fuer denselben Fall hat, wird nur der letzte (hoechste Laufnummer) zurueckgegeben
	 */
	@Nonnull
	List<String> getNeuesteVerfuegteAntraege(@Nonnull LocalDateTime verfuegtVon, @Nonnull LocalDateTime verfuegtBis);

	boolean isNeustesGesuch(@Nonnull Gesuch gesuch);

	/**
	 * Gibt das neueste (zuletzt verfügte) Gesuch für eine Gesuchsperiode und einen Fall zurueck.
	 */
	@Nonnull
	Optional<String> getNeustesFreigegebenesGesuchIdFuerGesuch(Gesuchsperiode gesuchsperiode, Fall fall);

	/**
	 * Schickt eine E-Mail an alle Gesuchsteller, die ihr Gesuch innerhalb einer konfigurierbaren Frist nach
	 * Erstellung nicht freigegeben haben.
	 * Gibt die Anzahl Warnungen zurueck.
	 */
	int warnGesuchNichtFreigegeben();

	/**
	 * Schickt eine E-Mail an alle Gesuchsteller, die die Freigabequittung innerhalb einer konfigurierbaren Frist nach
	 * Freigabe des Gesuchs nicht geschickt haben.
	 * Gibt die Anzahl Warnungen zurueck.
	 */
	int warnFreigabequittungFehlt();

	/**
	 * Löscht alle Gesuche, die nach einer konfigurierbaren Frist nach Erstellung nicht freigegeben bzw. nach Freigabe
	 * die Quittung nicht geschickt haben. Schickt dem Gesuchsteller eine E-Mail.
	 * Gibt die Anzahl Warnungen zurueck.
	 */
	int deleteGesucheOhneFreigabeOderQuittung();

	/**
	 * Prüft, ob alle Anträge dieser Periode im Status VERFUEGT oder NUR_SCHULAMT sind
	 */
	boolean canGesuchsperiodeBeClosed(@Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Sucht die neueste Online Mutation, die zu dem gegebenen Antrag gehoert und loescht sie.
	 * Diese Mutation muss Online und noch nicht freigegeben sein. Diese Methode darf nur bei ADMIN oder SUPER_ADMIN
	 * aufgerufen werden, wegen loescherechten wird es dann immer mir RunAs/SUPER_ADMIN) ausgefuehrt.
	 * @param antrag Der Antraege, zu denen die Mutation gehoert, die geloescht werden muss
	 */
	void removeOnlineMutation(@Nonnull Gesuch antrag);

	/**
	 * Sucht ein Folgegesuch fuer den gegebenen Antrag in der gegebenen Gesuchsperiode
	 * @param antrag Der Antraeg des Falles
	 * @param gesuchsperiode Gesuchsperiode in der das Folgegesuch gesucht werden muss
	 */
	void removeOnlineFolgegesuch(@Nonnull Gesuch antrag, @Nonnull Gesuchsperiode gesuchsperiode);

	/**
	 * Schliesst ein Gesuch, das sich im Status GEPRUEFT befindet und kein Angebot hat
	 * Das Gesuch bekommt den Status KEIN_ANGEBOT und der WizardStep VERFUEGEN den Status OK
	 */
	Gesuch closeWithoutAngebot(@Nonnull Gesuch gesuch);

	/**
	 * Wenn das Gesuch nicht nur Schulangebote hat, wechselt der Status auf VERFUEGEN. Falls es
	 * nur Schulangebote hat, wechselt der Status auf NUR_SCHULAMT, da es keine Verfuegung noetig ist
	 * @param gesuch
	 * @return
	 */
	Gesuch verfuegenStarten(@Nonnull Gesuch gesuch);

	void postGesuchVerfuegen(@Nonnull Gesuch gesuch);
}
