package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import ch.dvbern.ebegu.enums.AntragStatusDTO;

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
	private LocalDateTime timestampVon;

	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime timestampBis;

	@NotNull
	private AntragStatusDTO status;


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

	public LocalDateTime getTimestampVon() {
		return timestampVon;
	}

	public void setTimestampVon(LocalDateTime timestampVon) {
		this.timestampVon = timestampVon;
	}

	public LocalDateTime getTimestampBis() {
		return timestampBis;
	}

	public void setTimestampBis(LocalDateTime timestampBis) {
		this.timestampBis = timestampBis;
	}

	public AntragStatusDTO getStatus() {
		return status;
	}

	public void setStatus(AntragStatusDTO status) {
		this.status = status;
	}
}
