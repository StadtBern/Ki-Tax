package ch.dvbern.ebegu.dto.personensuche;

/**
 * Enum für Einwohnercodes aus dem EWK
 */
public enum EWKEinwohnercode {

	WOCHENAUFENTHALTER("01"),
	ABGEMELDET("02"),
	GESTORBEN("03"),
	EINWOHNER("05"),
	NICHT_EINWOHNER("06"),
	PROVISORISCH("07");

	private String code;

	EWKEinwohnercode(String code) {
		this.code = code;
	}

	public static EWKEinwohnercode getEWKEinwohnercode(String code) {
		for (EWKEinwohnercode ewkEinwohnercode : EWKEinwohnercode.values()) {
			if (ewkEinwohnercode.equals(code)) {
				return ewkEinwohnercode;
			}
		}
		return null;
	}
}
