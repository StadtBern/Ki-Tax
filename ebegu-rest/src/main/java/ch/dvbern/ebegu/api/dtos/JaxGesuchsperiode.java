package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;

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
	private GesuchsperiodeStatus status;


	public GesuchsperiodeStatus getStatus() {
		return status;
	}

	public void setStatus(GesuchsperiodeStatus status) {
		this.status = status;
	}
}
