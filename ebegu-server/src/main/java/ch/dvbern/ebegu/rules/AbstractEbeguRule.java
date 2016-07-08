package ch.dvbern.ebegu.rules;

import ch.dvbern.ebegu.types.DateRange;

import java.time.LocalDate;

/**
 * This defines a Rule that has a unique Name given by RuleKey. The Rule is valid for a specified validityPeriod and
 * is of a given type
 */
public abstract class AbstractEbeguRule implements Rule {

	/**
	 * This is the name of the Rule, Can be used to create messages etc.
	 */
	private RuleKey ruleKey;

	private RuleType ruleType;

	private DateRange validityPeriod;


	public AbstractEbeguRule(RuleKey ruleKey, RuleType ruleType, DateRange validityPeriod) {
		this.ruleKey = ruleKey;
		this.ruleType = ruleType;
		this.validityPeriod = validityPeriod;
	}

	@Override
	public LocalDate validFrom() {
		return validityPeriod.getGueltigAb();
	}

	@Override
	public LocalDate validTo() {
		return validityPeriod.getGueltigBis();
	}


	@Override
	public RuleType getRuleType() {
		return ruleType;
	}

	@Override
	public RuleKey getRuleKey() {
		return ruleKey;
	}
}
