package ch.dvbern.ebegu.dto.suchfilter.smarttable;

import org.apache.commons.lang3.StringUtils;
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

	private String fallNummer;     //Fall.fallnummer
	private String familienName;   //Gesuch.Gesuchsteller1.nachname bzw Gesuch.gesuchsteller2.nachname
	private String antragTyp;      //Gesuch.antragtyp
	private String gesuchsperiodeString; //Gesuch.gesuchperiode.gueltigAb nach jahr
	private String eingangsdatum;  //Gesuch.eingangsdatum
	private String aenderungsdatum;  //Gesuch.antragStatusHistory
	private String status;       //Gesuch.status
	private String angebote;        //Gesuch.kindContainers.betreuungen.institutionStammdaten.betreuungsangebotTyp
	private String institutionen;   //Gesuch.kindContainers.betreuungen.institutionStammdaten.institution.name
	private String verantwortlicher; //Fall.verwantwortlicher.name
	private String kinder; //Gesuch.kindContainers.kindJa.vorname

	public String getKinder() {
		return kinder;
	}

	public void setKinder(String kinder) {
		this.kinder = kinder;
	}

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

	public String getGesuchsperiodeString() {
		return gesuchsperiodeString;
	}

	public void setGesuchsperiodeString(String gesuchsperiodeString) {
		this.gesuchsperiodeString = gesuchsperiodeString;
	}

	public String getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(String eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	public String getAenderungsdatum() {
		return aenderungsdatum;
	}

	public void setAenderungsdatum(String aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum;
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
			.append("gesuchsperiodeString", gesuchsperiodeString)
			.append("eingangsdatum", eingangsdatum)
			.append("status", status)
			.append("angebote", angebote)
			.append("institutionen", institutionen)
			.append("verantwortlicher", verantwortlicher)
			.append("kinder", kinder)
			.toString();
	}

	public int readFallNummerAsNumber() {
		if (StringUtils.isNumeric(fallNummer)) {
			return Integer.valueOf(fallNummer);
		}
		return -1;

	}

	public String getFamilienNameForLike() {
		return StringUtils.isEmpty(familienName) ? null : familienName + '%';
	}

	public String getKindNameForLike() {
		return StringUtils.isEmpty(kinder) ? null : '%' + kinder + '%';
	}


}
