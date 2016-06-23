package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Finanzielle Situation
 */
@XmlRootElement(name = "einkommensverschlechterungContainer")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEinkommensverschlechterungContainer extends JaxAbstractDTO {


	private static final long serialVersionUID = -5547010246540824296L;


	@Valid
	@Nullable
	private JaxEinkommensverschlechterung ekvGSBasisJahrPlus1;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterung ekvGSBasisJahrPlus2;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterung ekvJABasisJahrPlus1;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterung ekvJABasisJahrPlus2;

	@Nullable
	public JaxEinkommensverschlechterung getEkvGSBasisJahrPlus1() {
		return ekvGSBasisJahrPlus1;
	}

	public void setEkvGSBasisJahrPlus1(@Nullable final JaxEinkommensverschlechterung ekvGSBasisJahrPlus1) {
		this.ekvGSBasisJahrPlus1 = ekvGSBasisJahrPlus1;
	}

	@Nullable
	public JaxEinkommensverschlechterung getEkvGSBasisJahrPlus2() {
		return ekvGSBasisJahrPlus2;
	}

	public void setEkvGSBasisJahrPlus2(@Nullable final JaxEinkommensverschlechterung ekvGSBasisJahrPlus2) {
		this.ekvGSBasisJahrPlus2 = ekvGSBasisJahrPlus2;
	}

	@Nullable
	public JaxEinkommensverschlechterung getEkvJABasisJahrPlus1() {
		return ekvJABasisJahrPlus1;
	}

	public void setEkvJABasisJahrPlus1(@Nullable final JaxEinkommensverschlechterung ekvJABasisJahrPlus1) {
		this.ekvJABasisJahrPlus1 = ekvJABasisJahrPlus1;
	}

	@Nullable
	public JaxEinkommensverschlechterung getEkvJABasisJahrPlus2() {
		return ekvJABasisJahrPlus2;
	}

	public void setEkvJABasisJahrPlus2(@Nullable final JaxEinkommensverschlechterung ekvJABasisJahrPlus2) {
		this.ekvJABasisJahrPlus2 = ekvJABasisJahrPlus2;
	}
}
