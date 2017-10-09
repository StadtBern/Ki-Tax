package ch.dvbern.ebegu.api.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.enums.ModulName;
import ch.dvbern.lib.date.converters.LocalTimeXMLConverter;

/**
 * DTO fuer Module fuer die Tagesschulen
 */
@XmlRootElement(name = "modul")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxModul extends JaxAbstractDTO {

	private static final long serialVersionUID = -1893537808325618626L;

	@NotNull
	private DayOfWeek wochentag;

	@NotNull
	private ModulName modulname;

	@NotNull
	@XmlJavaTypeAdapter(LocalTimeXMLConverter.class)
	private LocalTime zeitVon = null;

	@NotNull
	@XmlJavaTypeAdapter(LocalTimeXMLConverter.class)
	private LocalTime zeitBis = null;


	public DayOfWeek getWochentag() {
		return wochentag;
	}

	public void setWochentag(DayOfWeek wochentag) {
		this.wochentag = wochentag;
	}

	public ModulName getModulname() {
		return modulname;
	}

	public void setModulname(ModulName modulname) {
		this.modulname = modulname;
	}

	public LocalTime getZeitVon() {
		return zeitVon;
	}

	public void setZeitVon(LocalTime zeitVon) {
		this.zeitVon = zeitVon;
	}

	public LocalTime getZeitBis() {
		return zeitBis;
	}

	public void setZeitBis(LocalTime zeitBis) {
		this.zeitBis = zeitBis;
	}
}
