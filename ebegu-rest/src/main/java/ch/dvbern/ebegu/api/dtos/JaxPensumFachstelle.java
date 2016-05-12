package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Stammdaten der PensumFachstelle. Definiert ein bestimmtes Pensum und eine bestimmte Fachstelle und wird einem
 * Kind zugewiesen
 */
@XmlRootElement(name = "pensumFachstelle")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxPensumFachstelle extends JaxAbstractPensumDTO {

	private static final long serialVersionUID = -7997026881634137397L;

	private JaxFachstelle fachstelle;

	public JaxFachstelle getFachstelle() {
		return fachstelle;
	}

	public void setFachstelle(JaxFachstelle fachstelle) {
		this.fachstelle = fachstelle;
	}
}
