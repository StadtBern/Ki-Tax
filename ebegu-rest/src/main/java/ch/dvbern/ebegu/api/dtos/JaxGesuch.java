package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "gesuch")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuch extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217019901364130097L;

	@NotNull
	private JaxFall fall;

	@Nullable
	private JaxPerson gesuchssteller1;

	@Nullable
	private JaxPerson gesuchssteller2;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public JaxFall getFall() {
		return fall;
	}

	public void setFall(JaxFall fall) {
		this.fall = fall;
	}

	@Nullable
	public JaxPerson getGesuchssteller1() {
		return gesuchssteller1;
	}

	public void setGesuchssteller1(@Nullable JaxPerson gesuchssteller1) {
		this.gesuchssteller1 = gesuchssteller1;
	}

	@Nullable
	public JaxPerson getGesuchssteller2() {
		return gesuchssteller2;
	}

	public void setGesuchssteller2(@Nullable JaxPerson gesuchssteller2) {
		this.gesuchssteller2 = gesuchssteller2;
	}
}

