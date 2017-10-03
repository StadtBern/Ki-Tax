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
    FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
    IN_BEARBEITUNG_JA,
    ERSTE_MAHNUNG,
    ERSTE_MAHNUNG_ABGELAUFEN,
    ZWEITE_MAHNUNG,
    ZWEITE_MAHNUNG_ABGELAUFEN,
    GEPRUEFT,
    VERFUEGEN,
    VERFUEGT,
	KEIN_ANGEBOT,
	BESCHWERDE_HAENGIG,
	PRUEFUNG_STV,
	IN_BEARBEITUNG_STV,
	GEPRUEFT_STV;

	public static final Set<AntragStatus> FOR_ADMIN_ROLE = EnumSet.of(
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		KEIN_ANGEBOT,
		BESCHWERDE_HAENGIG,
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV);

	public static final Set<AntragStatus> FOR_INSTITUTION_ROLE = EnumSet.of(
		IN_BEARBEITUNG_GS,
		FREIGABEQUITTUNG,   // = GS hat Freigabequittung gedruckt, bzw. den Antrag freigegeben (auch wenn keine Freigabequittung notwendig ist)
		NUR_SCHULAMT,
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		BESCHWERDE_HAENGIG,
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV);

	public static final Set<AntragStatus> FOR_SCHULAMT_ROLE = EnumSet.of(
		NUR_SCHULAMT,
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		BESCHWERDE_HAENGIG,
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV);

	public static final Set<AntragStatus> FOR_STEUERAMT_ROLE = EnumSet.of(
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV);

	public static final Set<AntragStatus> FOR_JURIST_REVISOR_ROLE = EnumSet.of(
		NUR_SCHULAMT,
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		KEIN_ANGEBOT,
		BESCHWERDE_HAENGIG,
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV);


	public static final Set<AntragStatus> FIRST_STATUS_OF_VERFUEGT = EnumSet.of(VERFUEGT, NUR_SCHULAMT, KEIN_ANGEBOT);

    private static final Set<AntragStatus> all = EnumSet.allOf(AntragStatus.class);
    private static final Set<AntragStatus> none = EnumSet.noneOf(AntragStatus.class);
    private static final Set<AntragStatus> forAdminRole = FOR_ADMIN_ROLE;
	private static final Set<AntragStatus> forSachbearbeiterInstitutionRole = FOR_INSTITUTION_ROLE;
	private static final Set<AntragStatus> forSachbearbeiterTraegerschaftRole = FOR_INSTITUTION_ROLE;
    private static final Set<AntragStatus> forSachbearbeiterJugendamtRole = FOR_ADMIN_ROLE;
    private static final Set<AntragStatus> forSchulamtRole = FOR_SCHULAMT_ROLE;
    private static final Set<AntragStatus> forJuristRole = FOR_JURIST_REVISOR_ROLE;
    private static final Set<AntragStatus> forRevisorRole = FOR_JURIST_REVISOR_ROLE;
    private static final Set<AntragStatus> forSteueramt = FOR_STEUERAMT_ROLE;

    // range ist etwas gefaehrlich, da man sehr vorsichtig sein muss, in welcher Reihenfolge man die Werte schreibt. Ausserdem kann man
	// kein range mit Ausnahmen machen. In diesem Fall ist es deshalb besser ein .of zu benutzen

	public static final Set<AntragStatus> FOR_SACHBEARBEITER_JUGENDAMT_PENDENZEN = EnumSet.of(
		FREIGEGEBEN,
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		BESCHWERDE_HAENGIG,
		GEPRUEFT_STV);

	public static final Set<AntragStatus> FOR_KIND_DUBLETTEN = EnumSet.of(
		NUR_SCHULAMT,
		FREIGEGEBEN,
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		KEIN_ANGEBOT,
		BESCHWERDE_HAENGIG);

	public static final Set<AntragStatus> ERLEDIGTE_PENDENZ = EnumSet.of(VERFUEGT, NUR_SCHULAMT, KEIN_ANGEBOT);

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
			case ADMINISTRATOR_SCHULAMT: return forSchulamtRole;
            case STEUERAMT: return forSteueramt;
            default: return none;
        }
    }

    public static Set<AntragStatus> writeAllowedForRole(UserRole userRole) {
		switch (userRole) {
			case SUPER_ADMIN:
				return  all;
			case ADMIN:
			case SACHBEARBEITER_JA:
				return FOR_ADMIN_ROLE_WRITE;
			case GESUCHSTELLER:
				return FOR_GESUCHSTELLER_ROLE_WRITE;
			case SACHBEARBEITER_INSTITUTION:
			case SACHBEARBEITER_TRAEGERSCHAFT:
				return FOR_INSTITUTION_ROLE_WRITE;
			case STEUERAMT:
				return FOR_STEUERAMT_ROLE_WRITE;
			case SCHULAMT:
			case ADMINISTRATOR_SCHULAMT:
				return FOR_SCHULAMT_ROLE_WRITE;
			default:
				return none;
		}
	}

	public static Collection<AntragStatus> getAllVerfuegtStates() {
		return Arrays.asList(VERFUEGT, NUR_SCHULAMT, BESCHWERDE_HAENGIG,
			PRUEFUNG_STV, IN_BEARBEITUNG_STV, GEPRUEFT_STV, KEIN_ANGEBOT);
	}

	public static Collection<AntragStatus> getVerfuegtAndSTVStates() {
		return Arrays.asList(VERFUEGT, PRUEFUNG_STV, IN_BEARBEITUNG_STV, GEPRUEFT_STV);
	}

	public static Collection<AntragStatus> getInBearbeitungGSStates() {
		return Arrays.asList(FREIGABEQUITTUNG, IN_BEARBEITUNG_GS);
	}

	/**
	 * Ein verfuegtes Gesuch kann mehrere Status haben. Diese Methode immer anwenden um herauszufinden
	 * ob ein Gesuch verfuegt ist.
	 */
	public boolean isAnyStatusOfVerfuegt() {
		return getAllVerfuegtStates().contains(this);
	}

	/**
	 * Ein verfuegtes Gesuch kann mehrere Status haben. Diese Methode immer anwenden um herauszufinden
	 * ob ein Gesuch verfuegt ist.
	 */
	public boolean isAnyStatusOfVerfuegtOrVefuegen() {
		return getAllVerfuegtStates().contains(this) || this == VERFUEGEN;
	}

	public boolean inBearbeitung() { return inBearbeitung.contains(this); }

	public boolean isAnyOfInBearbeitungGS(){
		return this == FREIGABEQUITTUNG || this == IN_BEARBEITUNG_GS;
	}

	public boolean isAnyOfSchulamtOnly(){
		return this == NUR_SCHULAMT;
	}


	/**
	 *
	 * @return true wenn das Jugendamt das Gesuch oeffnen darf (Unsichtbar sind also Gesuch die von Gesuchsteller noch
	 * nicht eingereichte wurden und solche die NUR_SCHULAMT sind
	 */
	public boolean isReadableByJugendamtSteueramt() {
		//Jugendamt darf keine Gesuche sehen die noch nicht Freigegeben sind, und keine die nur Schulamt sind
		return !(this.isAnyOfInBearbeitungGS()) &&
			!(isAnyOfSchulamtOnly());
	}

	/**
	 * schulamt darf eigentlich alle Status lesen ausser denen die noch vom GS bearbeitet werden
	 */
	public boolean isReadableBySchulamtSachbearbeiter() {
		return !(this.isAnyOfInBearbeitungGS());
	}

	/**
	 * Steueramt darf nur die Status lesen die fuer es gemeint sind und auch den Status GEPRUEFT_STV
	 */
	public boolean isReadableBySteueramt() {
		// GEPRUEFT_STV ist dabei, weil es beim Freigeben schon in diesem Status ist
		return forSteueramt.contains(this) || this == GEPRUEFT_STV;
	}

	public boolean isReadableByJurist() {
		return forJuristRole.contains(this);
	}

	public boolean isReadableByRevisor() {
		return forRevisorRole.contains(this);
	}

	public static final Set<AntragStatus> FOR_ADMIN_ROLE_WRITE = EnumSet.of(
		FREIGABEQUITTUNG,
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN,
		VERFUEGT,
		KEIN_ANGEBOT,
		BESCHWERDE_HAENGIG,
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV);

	public static final Set<AntragStatus> FOR_INSTITUTION_ROLE_WRITE = EnumSet.of(
		IN_BEARBEITUNG_GS,
		FREIGABEQUITTUNG,   // = GS hat Freigabequittung gedruckt, bzw. den Antrag freigegeben (auch wenn keine Freigabequittung notwendig ist)
		FREIGEGEBEN,        // Freigabequittung im Jugendamt eingelesen ODER keine Quittung notwendig
		IN_BEARBEITUNG_JA,
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		GEPRUEFT,
		VERFUEGEN);

	public static final Set<AntragStatus> FOR_GESUCHSTELLER_ROLE_WRITE = EnumSet.of(
		IN_BEARBEITUNG_GS,
		NUR_SCHULAMT, // Damit eine Mutation erstellt werden kann
		FREIGABEQUITTUNG,
		NUR_SCHULAMT, // damit eine Mutation erstellt werden kann
		ERSTE_MAHNUNG,
		ERSTE_MAHNUNG_ABGELAUFEN,
		ZWEITE_MAHNUNG,
		ZWEITE_MAHNUNG_ABGELAUFEN,
		VERFUEGT // Damit eine Mutation erstellt werden kann
	);

	public static final Set<AntragStatus> FOR_SCHULAMT_ROLE_WRITE = EnumSet.of(
		FREIGABEQUITTUNG,
		NUR_SCHULAMT); // Wegen Dokumente-Upload Button

	public static final Set<AntragStatus> FOR_STEUERAMT_ROLE_WRITE = EnumSet.of(
		PRUEFUNG_STV,
		IN_BEARBEITUNG_STV,
		GEPRUEFT_STV // Der Status wird schon vor dem Speichern gesetzt. Falls dies mal in eine separate Methode kommt, kann dieser Status entfernt werden
	);
}
