package ch.dvbern.ebegu.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer AbstractAntragDTO
 */
@XmlRootElement(name = "abstAntragDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAbstractAntragDTO implements Serializable {

	private static final long serialVersionUID = -1277331654764135397L;

	@NotNull
	private long fallNummer;

	@NotNull
	private String familienName;

	protected String clazz;

	public JaxAbstractAntragDTO(String clazz) {
		this.clazz = clazz;
	}

	public long getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(long fallNummer) {
		this.fallNummer = fallNummer;
	}

	public String getFamilienName() {
		return familienName;
	}

	public void setFamilienName(String familienName) {
		this.familienName = familienName;
	}


}
