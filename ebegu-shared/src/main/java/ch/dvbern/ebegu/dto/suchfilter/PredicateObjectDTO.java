package ch.dvbern.ebegu.dto.suchfilter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Klasse zum deserialisieren/serialisieren des SmartTable Filter Objekts fuer suchfilter in Java
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class PredicateObjectDTO implements Serializable {

	private static final long serialVersionUID = -2248051428962150142L;

	private String fallNummer;
	private String familienName;
	private String antragTyp;
	private String gesuchsperiode;
	private String eingangsdatum;
	private String status;
	private String angebote;
	private String institutionen;
	private String verantwortlicher;

	public String getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(String fallNummer) {
		this.fallNummer = fallNummer;
	}

	public String getFamilienName() {
		return familienName;
	}

	public void setFamilienName(String familienName) {
		this.familienName = familienName;
	}

	public String getAntragTyp() {
		return antragTyp;
	}

	public void setAntragTyp(String antragTyp) {
		this.antragTyp = antragTyp;
	}

	public String getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(String gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	public String getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(String eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAngebote() {
		return angebote;
	}

	public void setAngebote(String angebote) {
		this.angebote = angebote;
	}

	public String getInstitutionen() {
		return institutionen;
	}

	public void setInstitutionen(String institutionen) {
		this.institutionen = institutionen;
	}

	public String getVerantwortlicher() {
		return verantwortlicher;
	}

	public void setVerantwortlicher(String verantwortlicher) {
		this.verantwortlicher = verantwortlicher;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("fallNummer", fallNummer)
			.append("familienName", familienName)
			.append("antragTyp", antragTyp)
			.append("gesuchsperiode", gesuchsperiode)
			.append("eingangsdatum", eingangsdatum)
			.append("status", status)
			.append("angebote", angebote)
			.append("institutionen", institutionen)
			.append("verantwortlicher", verantwortlicher)
			.toString();
	}
}
