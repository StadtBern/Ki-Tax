package ch.dvbern.ebegu.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dient dazu die Aufgabentypen zu unterscheiden
 */
public enum WorkJobType {

	REPORT_GENERATION( WorkJobConstants.REPORT_VORLAGE_TYPE_PARAM, WorkJobConstants.DATE_FROM_PARAM, WorkJobConstants.DATE_TO_PARAM, WorkJobConstants.GESUCH_PERIODE_ID_PARAM);

	List<String> paramNames = new ArrayList<>();


	WorkJobType(String... parameters) {
		paramNames.addAll(Arrays.asList(parameters));

	}

	public List<String> getParamNames() {
		return paramNames;
	}

}
