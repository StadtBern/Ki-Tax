package ch.dvbern.ebegu.dto.personensuche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO für Resultate aus dem EWK
 */
@XmlRootElement(name = "ewkResultat")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKResultat implements Serializable {

	private static final long serialVersionUID = 3663123555068820247L;

	private int maxResultate;

	private int anzahlResultate;

	private List<EWKPerson> personen = new ArrayList<>();


	public EWKResultat() {
	}

	public int getMaxResultate() {
		return maxResultate;
	}

	public void setMaxResultate(int maxResultate) {
		this.maxResultate = maxResultate;
	}

	public int getAnzahlResultate() {
		return anzahlResultate;
	}

	public void setAnzahlResultate(int anzahlResultate) {
		this.anzahlResultate = anzahlResultate;
	}

	public List<EWKPerson> getPersonen() {
		return personen;
	}

	public void setPersonen(List<EWKPerson> personen) {
		this.personen = personen;
	}
}
