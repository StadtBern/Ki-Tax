package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entitaet zum Speichern von zeitabhängigen Parametern in E-BEGU
 */
@Audited
@Entity
public class EbeguParameter extends AbstractDateRangedEntity {


	private static final long serialVersionUID = 8704632842261673111L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private EbeguParameterKey name;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	private String value;

	public EbeguParameter() {
	}

	public EbeguParameter(EbeguParameterKey name, String value) {
		this(name, value, Constants.DEFAULT_GUELTIGKEIT);
	}


	public EbeguParameter(EbeguParameterKey name, String value, DateRange gueltigkeit) {
		this.name = name;
		this.value = value;
		this.setGueltigkeit(gueltigkeit);
	}

	@Nonnull
	public EbeguParameterKey getName() {
		return name;
	}

	public void setName(@Nonnull EbeguParameterKey name) {
		this.name = name;
	}

	@Nonnull
	public String getValue() {
		return value;
	}

	public void setValue(@Nonnull String value) {
		this.value = value;
	}

	/**
	 * @param gueltigkeit
	 * @return a copy of the current Param with the gueltigkeit set to the passed DateRange
	 */
	public EbeguParameter copy(DateRange gueltigkeit) {
		EbeguParameter copiedParam = new EbeguParameter();
		copiedParam.setGueltigkeit(gueltigkeit);
		copiedParam.setName(this.getName());
		copiedParam.setValue(this.getValue());
		return copiedParam;
	}

	public BigDecimal getValueAsBigDecimal() {
		return new BigDecimal(value);
	}

	public Integer getValueAsInteger() {
		return Integer.valueOf(value);
	}
}
