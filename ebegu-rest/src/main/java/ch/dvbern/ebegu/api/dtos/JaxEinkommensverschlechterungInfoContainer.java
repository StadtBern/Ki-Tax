package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer EinkommensverschlechterungInfoContainer
 */
@XmlRootElement(name = "einkommensverschlechterunginfocontainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEinkommensverschlechterungInfoContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = 735198215986579755L;

	private JaxEinkommensverschlechterungInfo einkommensverschlechterungInfoGS;

	private JaxEinkommensverschlechterungInfo einkommensverschlechterungInfoJA;

	public JaxEinkommensverschlechterungInfo getEinkommensverschlechterungInfoGS() {
		return einkommensverschlechterungInfoGS;
	}

	public void setEinkommensverschlechterungInfoGS(JaxEinkommensverschlechterungInfo einkommensverschlechterungInfoGS) {
		this.einkommensverschlechterungInfoGS = einkommensverschlechterungInfoGS;
	}

	public JaxEinkommensverschlechterungInfo getEinkommensverschlechterungInfoJA() {
		return einkommensverschlechterungInfoJA;
	}

	public void setEinkommensverschlechterungInfoJA(JaxEinkommensverschlechterungInfo einkommensverschlechterungInfoJA) {
		this.einkommensverschlechterungInfoJA = einkommensverschlechterungInfoJA;
	}
}
