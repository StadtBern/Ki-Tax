package ch.dvbern.ebegu.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum fuer den Status vom Gesuch.
 */
public enum AntragStatus {
    IN_BEARBEITUNG_GS,
    FREIGABEQUITTUNG,
    NUR_SCHULAMT,
    FREIGEGEBEN,
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
    VERFUEGT;

    private static final Set<AntragStatus> all = EnumSet.allOf(AntragStatus.class);
    private static final Set<AntragStatus> none = EnumSet.noneOf(AntragStatus.class);
    private static final Set<AntragStatus> forAdminRole = EnumSet.range(FREIGEGEBEN, VERFUEGT);
	private static final Set<AntragStatus> forSachbearbeiterInstitutionRole = EnumSet.range(IN_BEARBEITUNG_GS, VERFUEGT);
	private static final Set<AntragStatus> forSachbearbeiterTraegerschaftRole = forSachbearbeiterInstitutionRole;
    private static final Set<AntragStatus> forSachbearbeiterJugendamtRole = forAdminRole;
    private static final Set<AntragStatus> forSchulamtRole = EnumSet.range(NUR_SCHULAMT, VERFUEGT);
    private static final Set<AntragStatus> forJuristRole = forSachbearbeiterJugendamtRole;
    private static final Set<AntragStatus> forRevisorRole = forAdminRole;

	private static final Set<AntragStatus> isFreigegeben = EnumSet.range(NUR_SCHULAMT, VERFUEGT);

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

	public boolean isFreigegeben() {
		return isFreigegeben.contains(this);
	}
}
