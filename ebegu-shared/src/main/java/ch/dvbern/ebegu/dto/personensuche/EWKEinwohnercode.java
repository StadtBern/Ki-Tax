package ch.dvbern.ebegu.dto.personensuche;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Enum für Einwohnercodes aus dem EWK
 */
@XmlRootElement(name = "ewkEinwohnercode")
@XmlAccessorType(XmlAccessType.FIELD)
public class EWKEinwohnercode implements Serializable {

	private static final long serialVersionUID = 4199345504567527849L;

	private String code;

	private String codeTxt;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigVon;

	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate gueltigBis;


	public EWKEinwohnercode() {
	}

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
