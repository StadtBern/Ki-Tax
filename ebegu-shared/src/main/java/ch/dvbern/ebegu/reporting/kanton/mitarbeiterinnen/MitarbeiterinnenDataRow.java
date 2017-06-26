package ch.dvbern.ebegu.reporting.kanton.mitarbeiterinnen;

import java.math.BigDecimal;

/**
 * DTO fuer die statistik der MitarbeiterInnen
 */
public class MitarbeiterinnenDataRow {

	private String name;
	private String vorname;
	private BigDecimal verantwortlicheGesuche;
	private BigDecimal verfuegungenAusgestellt;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public BigDecimal getVerantwortlicheGesuche() {
		return verantwortlicheGesuche;
	}

	public void setVerantwortlicheGesuche(BigDecimal verantwortlicheGesuche) {
		this.verantwortlicheGesuche = verantwortlicheGesuche;
	}

	public BigDecimal getVerfuegungenAusgestellt() {
		return verfuegungenAusgestellt;
	}

	public void setVerfuegungenAusgestellt(BigDecimal verfuegungenAusgestellt) {
		this.verfuegungenAusgestellt = verfuegungenAusgestellt;
	}
}
