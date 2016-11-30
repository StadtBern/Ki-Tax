package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "jaxFinSitModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFinanzModel {

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainerGS1;

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainerGS2;

	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS1;

	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS2;

	private JaxEinkommensverschlechterungInfo einkommensverschlechterungInfo;

	private boolean gemeinsameSteuererklaerung;

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainerGS1() {
		return finanzielleSituationContainerGS1;
	}

	public void setFinanzielleSituationContainerGS1(JaxFinanzielleSituationContainer finanzielleSituationContainerGS1) {
		this.finanzielleSituationContainerGS1 = finanzielleSituationContainerGS1;
	}

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainerGS2() {
		return finanzielleSituationContainerGS2;
	}

	public void setFinanzielleSituationContainerGS2(JaxFinanzielleSituationContainer finanzielleSituationContainerGS2) {
		this.finanzielleSituationContainerGS2 = finanzielleSituationContainerGS2;
	}

	public boolean isGemeinsameSteuererklaerung() {
		return gemeinsameSteuererklaerung;
	}

	public void setGemeinsameSteuererklaerung(boolean gemeinsameSteuererklaerung) {
		this.gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
	}

	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainerGS1() {
		return einkommensverschlechterungContainerGS1;
	}

	public void setEinkommensverschlechterungContainerGS1(JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS1) {
		this.einkommensverschlechterungContainerGS1 = einkommensverschlechterungContainerGS1;
	}

	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainerGS2() {
		return einkommensverschlechterungContainerGS2;
	}

	public void setEinkommensverschlechterungContainerGS2(JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerGS2) {
		this.einkommensverschlechterungContainerGS2 = einkommensverschlechterungContainerGS2;
	}

	public JaxEinkommensverschlechterungInfo getEinkommensverschlechterungInfo() {
		return einkommensverschlechterungInfo;
	}

	public void setEinkommensverschlechterungInfo(JaxEinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		this.einkommensverschlechterungInfo = einkommensverschlechterungInfo;
	}
}

