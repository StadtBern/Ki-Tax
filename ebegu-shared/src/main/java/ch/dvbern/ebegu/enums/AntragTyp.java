package ch.dvbern.ebegu.enums;

/**
 * Enum fuer das DTO JaxPendenzXX. Damit weisst man ob es sich um ein Gesuch oder eine Mutation handelt.
 */
public enum AntragTyp {

	ERSTGESUCH,
	MUTATION,
	ERNEUERUNGSGESUCH;

	public boolean isGesuch() {
		return ERSTGESUCH.equals(this) || ERNEUERUNGSGESUCH.equals(this);
	}

	public boolean isMutation() {
		return MUTATION.equals(this);
	}
}
