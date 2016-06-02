package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * DTO fuer Gesuchsperiode
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchsperiode extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -2495737706808699744L;

	@NotNull
	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
