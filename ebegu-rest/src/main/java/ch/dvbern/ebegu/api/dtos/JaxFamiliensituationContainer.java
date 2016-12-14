package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer FamiliensituationenContainer
 */
@XmlRootElement(name = "familiensituationContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFamiliensituationContainer extends JaxAbstractDTO{

	private static final long serialVersionUID = 5217224327005193232L;

	private JaxFamiliensituation familiensituationJA;

	private JaxFamiliensituation familiensituationGS;

	private JaxFamiliensituation familiensituationErstgesuch;

	public JaxFamiliensituation getFamiliensituationJA() {
		return familiensituationJA;
	}

	public void setFamiliensituationJA(JaxFamiliensituation familiensituationJA) {
		this.familiensituationJA = familiensituationJA;
	}

	public JaxFamiliensituation getFamiliensituationGS() {
		return familiensituationGS;
	}

	public void setFamiliensituationGS(JaxFamiliensituation familiensituationGS) {
		this.familiensituationGS = familiensituationGS;
	}

	public JaxFamiliensituation getFamiliensituationErstgesuch() {
		return familiensituationErstgesuch;
	}

	public void setFamiliensituationErstgesuch(JaxFamiliensituation familiensituationErstgesuch) {
		this.familiensituationErstgesuch = familiensituationErstgesuch;
	}
}
