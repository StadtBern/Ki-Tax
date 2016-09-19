package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import ch.dvbern.ebegu.enums.AntragStatus;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * DTO fuer AntragStatusHistory
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxAntragStatusHistory extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297026889674146397L;

	@NotNull
	private String gesuchId;

	@NotNull
	private JaxAuthLoginElement benutzer;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime datum;

	@NotNull
	private AntragStatus status;


	public String getGesuchId() {
		return gesuchId;
	}

	public void setGesuchId(String gesuchId) {
		this.gesuchId = gesuchId;
	}

	public JaxAuthLoginElement getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(JaxAuthLoginElement benutzer) {
		this.benutzer = benutzer;
	}

	public LocalDateTime getDatum() {
		return datum;
	}

	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

	public AntragStatus getStatus() {
		return status;
	}

	public void setStatus(AntragStatus status) {
		this.status = status;
	}
}
