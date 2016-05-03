package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Finanzielle Situation
 */
@XmlRootElement(name = "finanzielleSituation")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFinanzielleSituationContainer extends JaxAbstractDTO {


	private static final long serialVersionUID = -4812537186224986782L;

	@NotNull
	private Integer jahr;

	@Valid
	private JaxFinanzielleSituation finanzielleSituationGS;

	@Valid
	private JaxFinanzielleSituation finanzielleSituationJA;

	@Valid
	private JaxFinanzielleSituation finanzielleSituationSV;


	public Integer getJahr() {
		return jahr;
	}

	public void setJahr(Integer jahr) {
		this.jahr = jahr;
	}

	public JaxFinanzielleSituation getFinanzielleSituationGS() {
		return finanzielleSituationGS;
	}

	public void setFinanzielleSituationGS(JaxFinanzielleSituation finanzielleSituationGS) {
		this.finanzielleSituationGS = finanzielleSituationGS;
	}

	public JaxFinanzielleSituation getFinanzielleSituationJA() {
		return finanzielleSituationJA;
	}

	public void setFinanzielleSituationJA(JaxFinanzielleSituation finanzielleSituationJA) {
		this.finanzielleSituationJA = finanzielleSituationJA;
	}

	public JaxFinanzielleSituation getFinanzielleSituationSV() {
		return finanzielleSituationSV;
	}

	public void setFinanzielleSituationSV(JaxFinanzielleSituation finanzielleSituationSV) {
		this.finanzielleSituationSV = finanzielleSituationSV;
	}
}
