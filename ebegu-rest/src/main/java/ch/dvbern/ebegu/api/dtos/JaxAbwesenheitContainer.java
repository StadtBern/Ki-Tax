package ch.dvbern.ebegu.api.dtos;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Abwesenheit Container
 */
@XmlRootElement(name = "abwesenheit")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbwesenheitContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -8912537133244581785L;

	@Valid
	private JaxAbwesenheit abwensenheitGS;

	@Valid
	private JaxAbwesenheit abwesenheitJA;

	public JaxAbwesenheit getAbwensenheitGS() {
		return abwensenheitGS;
	}

	public void setAbwensenheitGS(JaxAbwesenheit abwensenheitGS) {
		this.abwensenheitGS = abwensenheitGS;
	}

	public JaxAbwesenheit getAbwesenheitJA() {
		return abwesenheitJA;
	}

	public void setAbwesenheitJA(JaxAbwesenheit abwesenheitJA) {
		this.abwesenheitJA = abwesenheitJA;
	}
}
