package ch.dvbern.ebegu.dto.dataexport.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

/**
 * This is a DTO that is used to export the relevant Information about a {@link ch.dvbern.ebegu.entities.Gesuchsteller}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GesuchstellerExportDTO {

	private String vorname;
	private String nachname;
	private String email;


	public GesuchstellerExportDTO() {
	}

	public GesuchstellerExportDTO(String vorname, String nachname, String mail) {
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = mail;

	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GesuchstellerExportDTO that = (GesuchstellerExportDTO) o;
		return Objects.equals(getVorname(), that.getVorname()) &&
			Objects.equals(getNachname(), that.getNachname()) &&
			Objects.equals(getEmail(), that.getEmail());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getVorname(), getNachname(), getEmail());
	}
}
