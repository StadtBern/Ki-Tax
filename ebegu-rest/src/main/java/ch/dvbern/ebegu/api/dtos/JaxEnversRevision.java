package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;
import org.hibernate.envers.RevisionType;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * DTO fuer Application Propertie
 */
@XmlRootElement(name = "enversRevision")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEnversRevision extends JaxAbstractDTO {

	private static final long serialVersionUID = 7069586216789441872L;

	@NotNull
	private int rev;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime revTimeStamp;

	private JaxAbstractDTO entity;
	private RevisionType accessType;


	public JaxAbstractDTO getEntity() {
		return entity;
	}

	public void setEntity(JaxAbstractDTO entity) {
		this.entity = entity;
	}

	public int getRev() {
		return rev;
	}

	public void setRev(int rev) {
		this.rev = rev;
	}

	public LocalDateTime getRevTimeStamp() {
		return revTimeStamp;
	}

	public void setRevTimeStamp(LocalDateTime revTimeStamp) {
		this.revTimeStamp = revTimeStamp;
	}

	public void setAccessType(RevisionType accessType) {
		this.accessType = accessType;
	}

	public RevisionType getAccessType() {
		return accessType;
	}
}
