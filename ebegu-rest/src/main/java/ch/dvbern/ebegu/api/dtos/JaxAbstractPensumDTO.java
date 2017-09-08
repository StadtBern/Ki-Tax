package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Superklasse fuer ein Pensum
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbstractPensumDTO extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -7598194821332548948L;

	@Min(0)
	@Max(100)
	@NotNull
	private Integer pensum;

	public Integer getPensum() {
		return pensum;
	}

	public void setPensum(Integer pensum) {
		this.pensum = pensum;
	}
}
