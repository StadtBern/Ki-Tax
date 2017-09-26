package ch.dvbern.ebegu.enums;


import java.util.EnumSet;
import java.util.Set;

/**
 * Enum fuers Feld status in einer Betreuung.
 */
public enum Betreuungsstatus {

	@Deprecated //wir glauben das gibts gar nicht mehr
	AUSSTEHEND,
	WARTEN,
	SCHULAMT,
	ABGEWIESEN,
	NICHT_EINGETRETEN,
	STORNIERT,
	BESTAETIGT,
	VERFUEGT,
	GESCHLOSSEN_OHNE_VERFUEGUNG;

	private static final Set<Betreuungsstatus> all = EnumSet.allOf(Betreuungsstatus.class);
	private static final Set<Betreuungsstatus> none = EnumSet.noneOf(Betreuungsstatus.class);

	private static final Set<Betreuungsstatus> forSachbearbeiterInstitutionRole = EnumSet.of(WARTEN, VERFUEGT, BESTAETIGT, ABGEWIESEN, NICHT_EINGETRETEN, STORNIERT, GESCHLOSSEN_OHNE_VERFUEGUNG);
	public static final Set<Betreuungsstatus> hasVerfuegung = EnumSet.of(VERFUEGT, NICHT_EINGETRETEN);

	private static final Set<Betreuungsstatus> forSachbearbeiterTraegerschaftRole = forSachbearbeiterInstitutionRole;


	public boolean isGeschlossen() {
		return VERFUEGT.equals(this) || GESCHLOSSEN_OHNE_VERFUEGUNG.equals(this) || NICHT_EINGETRETEN.equals(this);
	}

	public boolean isAnyStatusOfVerfuegt() {
		return VERFUEGT.equals(this) || STORNIERT.equals(this) || SCHULAMT.equals(this);
	}

	public boolean isSendToInstitution(){
		return ABGEWIESEN.equals(this) || BESTAETIGT.equals(this)|| WARTEN.equals(this);
	}

	@SuppressWarnings("Duplicates")
	public static Set<Betreuungsstatus> allowedRoles(UserRole userRole) {
		switch (userRole) {
			case SUPER_ADMIN:
				return all;
			case ADMIN:
				return all;
			case GESUCHSTELLER:
				return all;
			case JURIST:
				return all;
			case REVISOR:
				return all;
			case SACHBEARBEITER_INSTITUTION:
				return forSachbearbeiterInstitutionRole;
			case SACHBEARBEITER_JA:
				return all;
			case SACHBEARBEITER_TRAEGERSCHAFT:
				return forSachbearbeiterTraegerschaftRole;
			case SCHULAMT:
				return all;
			case STEUERAMT:
				return all;
			default:
				return none;
		}
	}
}
