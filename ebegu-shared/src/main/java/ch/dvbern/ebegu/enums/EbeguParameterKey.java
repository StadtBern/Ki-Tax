package ch.dvbern.ebegu.enums;

/**
 * Keys für die zeitabhängigen E-BEGU-Parameter
 */
public enum EbeguParameterKey {

	// Fixbeitrag der Stadt pro Tag, nur für Kitas relevant
	PARAM_FIXBETRAG_STADT_PRO_TAG_KITA, // CHF 7

	// Anzahl Tage Kita Max
	PARAM_ANZAL_TAGE_MAX_KITA, // 244

	// Stunden / Tag Max
	PARAM_STUNDEN_PRO_TAG_MAX_KITA, // 11.5

	// Kosten pro Stunde Max
	PARAM_KOSTEN_PRO_STUNDE_MAX, // CHF 11.91

	// Kosten pro Stunde Max Tageseltern
	PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN, // CHF 9.16

	// Kosten pro Stunde Min
	PARAM_KOSTEN_PRO_STUNDE_MIN, // CHF 0.75

	// Minimal Massgebendes Einkommen
	PARAM_MASSGEBENDES_EINKOMMEN_MIN, // CHF 42540

	// Maximal Massgebendes Einkommen
	PARAM_MASSGEBENDES_EINKOMMEN_MAX, // CHF 158690

	// Anzahl Tage Kanton
	PARAM_ANZAHL_TAGE_KANTON, // 240

	// Stunden / Tag Tagi
	PARAM_STUNDEN_PRO_TAG_TAGI, // 7

	// Min Pensum Tagesstätten
	PARAM_PENSUM_TAGI_MIN, // 60

	// Min Pensum Kitas
	PARAM_PENSUM_KITA_MIN, // 10

	// Min Pensum Tageseltern
	PARAM_PENSUM_TAGESELTERN_MIN, // 20

	// Pauschalabzug bei einer Familiengrösse von drei Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3, // 3760

	// Pauschalabzug bei einer Familiengrösse von vier Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4, // 5900

	// Pauschalabzug bei einer Familiengrösse von fünf Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5, // 6970

	// Pauschalabzug bei einer Familiengrösse von sechs Personen pauschal pro Person
	PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6, // 7500

	// Max Abwesenheit
	PARAM_MAX_TAGE_ABWESENHEIT, // 30

	// Abgeltung des Kantons pro Tag (Achtung: Kann auf den 1.1. ändern!)
	PARAM_ABGELTUNG_PRO_TAG_KANTON, // 107.19

}
