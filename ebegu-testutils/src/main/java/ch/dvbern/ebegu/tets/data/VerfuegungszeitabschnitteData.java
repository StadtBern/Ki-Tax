package ch.dvbern.ebegu.tets.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Root XML Klasse zum speichern aller Verfuegungszeitabschnitte in XML file
 */
@XmlRootElement(name = "verfuegungszeitAbschnitte")
@XmlAccessorType(XmlAccessType.FIELD)
public class VerfuegungszeitabschnitteData {

	private Integer nummer;

	private String nameKind;

	private String nameBetreung;

	private List<VerfuegungZeitabschnittData> verfuegungszeitabschnitte = new ArrayList<>();

	public List<VerfuegungZeitabschnittData> getVerfuegungszeitabschnitte() {
		return verfuegungszeitabschnitte;
	}

	public void setVerfuegungszeitabschnitte(List<VerfuegungZeitabschnittData> verfuegungszeitabschnitte) {
		this.verfuegungszeitabschnitte = verfuegungszeitabschnitte;
	}

	public Integer getNummer() {
		return nummer;
	}

	public void setNummer(Integer nummer) {
		this.nummer = nummer;
	}

	public String getNameKind() {
		return nameKind;
	}

	public void setNameKind(String nameKind) {
		this.nameKind = nameKind;
	}

	public String getNameBetreung() {
		return nameBetreung;
	}

	public void setNameBetreung(String nameBetreung) {
		this.nameBetreung = nameBetreung;
	}
}
