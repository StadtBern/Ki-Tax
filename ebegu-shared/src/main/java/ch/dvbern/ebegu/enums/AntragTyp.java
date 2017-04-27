package ch.dvbern.ebegu.enums;

import java.util.ArrayList;
import java.util.List;

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

	public static List<AntragTyp> getValuesForFilter(String name) {
		List<AntragTyp> values = new ArrayList<>();
		// Im Tabellenfilter wird nicht zwischen ERSTGESUCH und ERNEUERUNGSGESUCH unterschieden
		if (AntragTyp.valueOf(name).equals(ERSTGESUCH)) {
			values.add(ERSTGESUCH);
			values.add(ERNEUERUNGSGESUCH);
		} else {
			values.add(AntragTyp.valueOf(name));
		}
		return values;
	}
}
