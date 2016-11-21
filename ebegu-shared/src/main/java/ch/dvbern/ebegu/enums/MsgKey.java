package ch.dvbern.ebegu.enums;

import ch.dvbern.ebegu.rules.RuleKey;


/**
 * Dieses Enum dient der Verwaltung von Server Seitigen Uebersetzbaren Messages. Die hier definierten keys sollten im
 * server-messages.properties file uebersetzt werden. Die Optionale Verklinkung mit anderen Enums ist rein informativ
 */
public enum MsgKey {


	ERWERBSPENSUM_GS1_MSG(RuleKey.ERWERBSPENSUM),
	ERWERBSPENSUM_GS2_MSG(RuleKey.ERWERBSPENSUM),
	ERWERBSPENSUM_ANSPRUCH(RuleKey.ERWERBSPENSUM),
	BETREUUNGSANGEBOT_MSG(RuleKey.BETREUUNGSANGEBOT_TYP),
	BETREUUNGSPENSUM_MSG(RuleKey.BETREUUNGSPENSUM),
	EINKOMMEN_MSG(RuleKey.EINKOMMEN),
	EINKOMMEN_VOLLKOSTEN_MSG(RuleKey.EINKOMMEN),
	EINKOMMENSVERSCHLECHTERUNG1_ACCEPT_MSG(RuleKey.EINKOMMEN),
	EINKOMMENSVERSCHLECHTERUNG2_ACCEPT_MSG(RuleKey.EINKOMMEN),
	EINKOMMENSVERSCHLECHTERUNG1_NOT_ACCEPT_MSG(RuleKey.EINKOMMEN),
	EINKOMMENSVERSCHLECHTERUNG2_NOT_ACCEPT_MSG(RuleKey.EINKOMMEN),

	MINDESTALTER_MSG(RuleKey.MINDESTALTER),

	WOHNHAFT_MSG(RuleKey.WOHNHAFT_IM_GLEICHEN_HAUSHALT),
	WOHNSITZ_MSG(RuleKey.WOHNSITZ),
	FACHSTELLE_MSG(RuleKey.FACHSTELLE),
	EINREICHUNGSFRIST_MSG(RuleKey.EINREICHUNGSFRIST),
	EINREICHUNGSFRIST_VOLLKOSTEN_MSG(RuleKey.EINREICHUNGSFRIST),
	RESTANSPRUCH_MSG(RuleKey.RESTANSPRUCH),
	REDUCKTION_RUECKWIRKEND_MSG(RuleKey.ANSPRUCHSBERECHNUNGSREGELN_MUTATIONEN),
	ANSPRUCHSAENDERUNG_MSG(RuleKey.ANSPRUCHSBERECHNUNGSREGELN_MUTATIONEN),

	GEKUENDIGT_VOR_EINTRITT_MSG(RuleKey.GEKUENDIGT_VOR_EINTRITT);

	//todo Mutation
	//Abwesenheit
	//Familiensituation

	private RuleKey referencedRuleKey;

	MsgKey(RuleKey referecedRule) {
		this.referencedRuleKey = referecedRule;
	}

	MsgKey() {
	}

	public RuleKey getReferencedRuleKey() {
		return referencedRuleKey;
	}

	public void setReferencedRuleKey(RuleKey referencedRuleKey) {
		this.referencedRuleKey = referencedRuleKey;
	}
}
