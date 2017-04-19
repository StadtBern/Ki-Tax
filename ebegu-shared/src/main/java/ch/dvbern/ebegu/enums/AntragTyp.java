package ch.dvbern.ebegu.enums;

/**
 * Enum fuer das DTO JaxPendenzXX. Damit weisst man ob es sich um ein Gesuch oder eine Mutation handelt.
 */
public enum AntragTyp {

	GESUCH, //TODO (hefr) umbenennen nach ERSTGESUCH inkl. dbskript
	MUTATION,
	ERNEUERUNGSGESUCH;

	public boolean isGesuch() {
		return GESUCH.equals(this) || ERNEUERUNGSGESUCH.equals(this);
	}

	public boolean isMutation() {
		return MUTATION.equals(this);
	}
}
