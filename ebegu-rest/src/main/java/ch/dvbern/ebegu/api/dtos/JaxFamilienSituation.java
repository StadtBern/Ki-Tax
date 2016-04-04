package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.enums.EnumBeantragen;
import ch.dvbern.ebegu.enums.EnumFamiliensituation;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Familiensituationen
 */
@XmlRootElement(name = "familiensituation")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFamilienSituation extends JaxAbstractDTO{

	private static final long serialVersionUID = -1297019741664130597L;

	@NotNull
	private EnumFamiliensituation familiensituation;

	@Nullable
	private EnumBeantragen beantragen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String bemerkungen;

	@NotNull
	private JaxGesuch gesuch;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public EnumFamiliensituation getFamiliensituation() {
		return familiensituation;
	}

	public void setFamiliensituation(EnumFamiliensituation familiensituation) {
		this.familiensituation = familiensituation;
	}

	@Nullable
	public EnumBeantragen getBeantragen() {
		return beantragen;
	}

	public void setBeantragen(@Nullable EnumBeantragen beantragen) {
		this.beantragen = beantragen;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	public JaxGesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(JaxGesuch gesuch) {
		this.gesuch = gesuch;
	}
}
