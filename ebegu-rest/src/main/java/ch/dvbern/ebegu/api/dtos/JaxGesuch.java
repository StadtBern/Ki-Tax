package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "gesuch")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuch extends JaxAbstractAntragDTO {

	private static final long serialVersionUID = -1217019901364130097L;


	@Nullable
	private JaxGesuchsteller gesuchsteller1;

	@Nullable
	private JaxGesuchsteller gesuchsteller2;

	@NotNull
	private Set<JaxKindContainer> kinder = new HashSet<>();

	private Boolean einkommensverschlechterung;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Nullable
	public JaxGesuchsteller getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable JaxGesuchsteller gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public JaxGesuchsteller getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable JaxGesuchsteller gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<JaxKindContainer> getKinder() {
		return kinder;
	}

	public void setKinder(Set<JaxKindContainer> kinder) {
		this.kinder = kinder;
	}

	public Boolean getEinkommensverschlechterung() {
		return einkommensverschlechterung;
	}

	public void setEinkommensverschlechterung(Boolean einkommensverschlechterung) {
		this.einkommensverschlechterung = einkommensverschlechterung;
	}
}

