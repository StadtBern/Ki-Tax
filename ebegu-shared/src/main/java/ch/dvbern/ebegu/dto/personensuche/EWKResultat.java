package ch.dvbern.ebegu.dto.personensuche;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO für Resultate aus dem EWK
 */
public class EWKResultat {

	private int maxResultate;
	private int anzahlResultate;
	private List<EWKPerson> personen = new ArrayList<>();

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
