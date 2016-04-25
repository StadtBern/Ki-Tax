package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_SHORT_LENGTH;

/**
 * DTO fuer Stammdaten der Fachstelle
 */
@XmlRootElement(name = "fachstelle")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFachstelle extends JaxAbstractDTO {

	private static final long serialVersionUID = -1277026901764135397L;

	@Size(min = 1, max = DB_DEFAULT_SHORT_LENGTH)
	@NotNull
	private String name;

	@Size(max = DB_DEFAULT_MAX_LENGTH)
	private String beschreibung;

	private boolean behinderungsbestaetigung;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public boolean isBehinderungsbestaetigung() {
		return behinderungsbestaetigung;
	}

	public void setBehinderungsbestaetigung(boolean behinderungsbestaetigung) {
		this.behinderungsbestaetigung = behinderungsbestaetigung;
	}
}
