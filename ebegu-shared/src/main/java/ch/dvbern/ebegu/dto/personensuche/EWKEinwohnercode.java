package ch.dvbern.ebegu.dto.personensuche;

import java.time.LocalDate;

/**
 * Enum für Einwohnercodes aus dem EWK
 */
public class EWKEinwohnercode {

	private String code;
	private String codeTxt;
	private LocalDate gueltigVon;
	private LocalDate gueltigBis;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeTxt() {
		return codeTxt;
	}

	public void setCodeTxt(String codeTxt) {
		this.codeTxt = codeTxt;
	}

	public LocalDate getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(LocalDate gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public LocalDate getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(LocalDate gueltigBis) {
		this.gueltigBis = gueltigBis;
	}
}
