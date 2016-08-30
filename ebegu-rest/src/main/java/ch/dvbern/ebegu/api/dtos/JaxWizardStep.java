package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.enums.WizardStepStatus;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer JaxWizardStep
 */
@XmlRootElement(name = "wzardStep")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxWizardStep extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217019901364138697L;

	@NotNull
	private String gesuchId; // we don't need the complete gesuch but just its Id

	@NotNull
	private WizardStepName wizardStepName;

	@NotNull
	private WizardStepStatus wizardStepStatus;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String bemerkungen;



	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public WizardStepName getWizardStepName() {
		return wizardStepName;
	}

	public void setWizardStepName(WizardStepName wizardStepName) {
		this.wizardStepName = wizardStepName;
	}

	public WizardStepStatus getWizardStepStatus() {
		return wizardStepStatus;
	}

	public void setWizardStepStatus(WizardStepStatus wizardStepStatus) {
		this.wizardStepStatus = wizardStepStatus;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}
}
