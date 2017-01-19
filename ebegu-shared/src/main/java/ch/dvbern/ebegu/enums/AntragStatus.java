package ch.dvbern.ebegu.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum fuer den Status vom Gesuch.
 */
public enum AntragStatus {
    IN_BEARBEITUNG_GS,
    FREIGABEQUITTUNG,   // = GS hat Freigabequittung gedruckt, bzw. den Antrag freigegeben (auch wenn keine Freigabequittung notwendig ist)
    NUR_SCHULAMT,
	NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN,
    FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
    IN_BEARBEITUNG_JA,
    ZURUECKGEWIESEN,
    ERSTE_MAHNUNG,
	ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
    ERSTE_MAHNUNG_ABGELAUFEN,
    ZWEITE_MAHNUNG,
    ZWEITE_MAHNUNG_ABGELAUFEN,
	ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
    GEPRUEFT,
    VERFUEGEN,
    VERFUEGT,
	BESCHWERDE_HAENGIG;

    private static final Set<AntragStatus> all = EnumSet.allOf(AntragStatus.class);
    private static final Set<AntragStatus> none = EnumSet.noneOf(AntragStatus.class);
    private static final Set<AntragStatus> forAdminRole = EnumSet.range(FREIGEGEBEN, BESCHWERDE_HAENGIG);
	private static final Set<AntragStatus> forSachbearbeiterInstitutionRole = EnumSet.range(IN_BEARBEITUNG_GS, BESCHWERDE_HAENGIG);
	private static final Set<AntragStatus> forSachbearbeiterTraegerschaftRole = forSachbearbeiterInstitutionRole;
    private static final Set<AntragStatus> forSachbearbeiterJugendamtRole = forAdminRole;
    private static final Set<AntragStatus> forSchulamtRole = EnumSet.range(NUR_SCHULAMT, VERFUEGT);
    private static final Set<AntragStatus> forJuristRole = forSachbearbeiterJugendamtRole;
    private static final Set<AntragStatus> forRevisorRole = forAdminRole;

    // range ist etwas gefaehrlich, da man sehr vorsichtig sein muss, in welcher Reihenfolge man die Werte schreibt. Ausserdem kann man
	// kein range mit Ausnahmen machen. In diesem Fall ist es deshalb besser ein .of zu benutzen
	public static final Set<AntragStatus> FOR_SACHBEARBEITER_JUGENDAMT_PENDENZEN = EnumSet.of(FREIGEGEBEN,
		IN_BEARBEITUNG_JA,
		ZURUECKGEWIESEN,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
		GEPRUEFT,
		VERFUEGEN,
		BESCHWERDE_HAENGIG);

	private static final Set<AntragStatus> inBearbeitung = EnumSet.range(IN_BEARBEITUNG_GS, IN_BEARBEITUNG_JA);



    /**
     * Implementierung eines Berechtigungskonzepts fuer die Antragssuche.
     *
     * @param userRole die Rolle
     * @return Liefert die einsehbaren Antragsstatus fuer die Rolle
     */
    @SuppressWarnings("Duplicates")
	public static Set<AntragStatus> allowedforRole(UserRole userRole) {
        switch (userRole) {
			case SUPER_ADMIN: return  all;
			case ADMIN: return forAdminRole;
            case GESUCHSTELLER: return none;
            case JURIST: return forJuristRole;
            case REVISOR: return forRevisorRole;
            case SACHBEARBEITER_INSTITUTION: return forSachbearbeiterInstitutionRole;
            case SACHBEARBEITER_JA: return forSachbearbeiterJugendamtRole;
            case SACHBEARBEITER_TRAEGERSCHAFT: return forSachbearbeiterTraegerschaftRole;
            case SCHULAMT: return forSchulamtRole;
            case STEUERAMT: return none;
            default: return none;
        }
    }

	public static Collection<AntragStatus> getAllVerfuegtStates() {
		return Arrays.asList(VERFUEGT, NUR_SCHULAMT, NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN, BESCHWERDE_HAENGIG);
	}

	/**
	 * Ein verfuegtes Gesuch kann mehrere Status haben. Diese Methode immer anwenden um herauszufinden
	 * ob ein Gesuch verfuegt ist.
	 */
	public boolean isAnyStatusOfVerfuegt() {
		return getAllVerfuegtStates().contains(this);
	}

	public boolean inBearbeitung() { return inBearbeitung.contains(this); }

}
