package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO fuer Stammdaten der Betreuungsmitteilung
 */
@XmlRootElement(name = "betreuungsmitteilung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBetreuungsmitteilung extends JaxMitteilung {

	private static final long serialVersionUID = -1297781341675137397L;

	@NotNull
	private List<JaxBetreuungsmitteilungPensum> betreuungspensen = new ArrayList<>();

	@NotNull
	private Boolean applied = false;

	public List<JaxBetreuungsmitteilungPensum> getBetreuungspensen() {
		return betreuungspensen;
	}

	public void setBetreuungspensen(List<JaxBetreuungsmitteilungPensum> betreuungspensen) {
		this.betreuungspensen = betreuungspensen;
	}

	public Boolean getApplied() {
		return applied;
	}

	public void setApplied(Boolean applied) {
		this.applied = applied;
	}
}
