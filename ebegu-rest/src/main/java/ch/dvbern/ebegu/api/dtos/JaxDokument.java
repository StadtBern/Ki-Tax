package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlRootElement(name = "dokument")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokument extends JaxFile {

	private static final long serialVersionUID = 1118235796540488553L;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime timestampUpload;

	public LocalDateTime getTimestampUpload() {
		return timestampUpload;
	}

	public void setTimestampUpload(LocalDateTime timestampUpload) {
		this.timestampUpload = timestampUpload;
	}
}
