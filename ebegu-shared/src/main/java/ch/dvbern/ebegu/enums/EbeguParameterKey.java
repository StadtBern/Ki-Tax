package ch.dvbern.ebegu.enums;

/**
 * Keys für die zeitabhängigen E-BEGU-Parameter
 */
public enum EbeguParameterKey {

	// Fixbeitrag der Stadt pro Tag, nur für Kitas relevant
	PARAM_FIXBETRAG_STADT_PRO_TAG_KITA,

	// Anzahl Tage Kita Max
	PARAM_ANZAL_TAGE_MAX_KITA,

	// Stunden / Tag Max
	PARAM_STUNDEN_PRO_TAG_MAX_KITA,

	// Kosten pro Stunde Max
	PARAM_KOSTEN_PRO_STUNDE_MAX,

	// Kosten pro Stunde Max Tageseltern
	PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN,

	// Kosten pro Stunde Min
	PARAM_KOSTEN_PRO_STUNDE_MIN,

	// Minimal Massgebendes Einkommen
	PARAM_MASSGEBENDES_EINKOMMEN_MIN,

	// Maximal Massgebendes Einkommen
	PARAM_MASSGEBENDES_EINKOMMEN_MAX,

	// Anzahl Tage Kanton
	PARAM_ANZAHL_TAGE_KANTON,

	// Stunden / Tag Tagi
	PARAM_STUNDEN_PRO_TAG_TAGI,

	// Min Pensum Tagesstätten
	PARAM_PENSUM_TAGI_MIN,

	// Min Pensum Kitas
	PARAM_PENSUM_KITA_MIN,

	// Min Pensum Tageseltern
	PARAM_PENSUM_TAGESELTERN_MIN,

	// Min Pensum Tagesschule
	PARAM_PENSUM_TAGESSCHULE_MIN,

	// Pauschalabzug bei einer Familiengrösse von drei Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3,

	// Pauschalabzug bei einer Familiengrösse von vier Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4,

	// Pauschalabzug bei einer Familiengrösse von fünf Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5,

	// Pauschalabzug bei einer Familiengrösse von sechs Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6,

	// Max Abwesenheit
	PARAM_MAX_TAGE_ABWESENHEIT,

	// Abgeltung des Kantons pro Tag (Achtung: Kann auf den 1.1. aendern! Im Gegensatz zu den anderen Parametern)
	PARAM_ABGELTUNG_PRO_TAG_KANTON(Boolean.FALSE);

	private boolean proGesuchsperiode;

	EbeguParameterKey() {
		this.proGesuchsperiode = true;
	}

	EbeguParameterKey(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}
}
