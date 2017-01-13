package ch.dvbern.ebegu.dto;

import ch.dvbern.ebegu.enums.MsgKey;
import ch.dvbern.ebegu.rules.RuleKey;

/**
 * DTO für eine Verfügungsbemerkung
 */
public class VerfuegungsBemerkung {

	private RuleKey ruleKey;
	private MsgKey msgKey;

	public VerfuegungsBemerkung(RuleKey ruleKey, MsgKey msgKey) {
		this.ruleKey = ruleKey;
		this.msgKey = msgKey;
	}

	public RuleKey getRuleKey() {
		return ruleKey;
	}

	public void setRuleKey(RuleKey ruleKey) {
		this.ruleKey = ruleKey;
	}

	public MsgKey getMsgKey() {
		return msgKey;
	}

	public void setMsgKey(MsgKey msgKey) {
		this.msgKey = msgKey;
	}
}
