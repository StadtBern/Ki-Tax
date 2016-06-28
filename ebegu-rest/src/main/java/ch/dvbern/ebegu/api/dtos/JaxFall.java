package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "fall")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFall extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297019901664130597L;

	@Nullable
	@NotNull
	private int fallNummer;

	@NotNull
	private JaxAuthLoginElement verantwortlicher;


	@Min(1)
	private Integer nextNumberKind = 1;


	@Nullable
	public int getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(@Nullable int fallNummer) {
		this.fallNummer = fallNummer;
	}

	public JaxAuthLoginElement getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(JaxAuthLoginElement verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}

	public Integer getNextNumberKind() {
		return nextNumberKind;
	}

	public void setNextNumberKind(Integer nextNumberKind) {
		this.nextNumberKind = nextNumberKind;
	}

}
