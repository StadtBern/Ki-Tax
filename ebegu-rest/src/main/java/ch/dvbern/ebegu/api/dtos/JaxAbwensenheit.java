package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Daten der Abwesenheit,
 */
@XmlRootElement(name = "abwesenheit")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbwensenheit extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -1117022381674937847L;

}
