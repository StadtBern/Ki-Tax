package ch.dvbern.ebegu.api.dtos;

import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;

@XmlRootElement(name = "dokument")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxDokument extends JaxFile {

	private static final long serialVersionUID = 1118235796540488553L;

	@NotNull
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private LocalDateTime timestampUpload;

	@Nullable
	private JaxAuthLoginElement userUploaded;

	public LocalDateTime getTimestampUpload() {
		return timestampUpload;
	}

	public void setTimestampUpload(LocalDateTime timestampUpload) {
		this.timestampUpload = timestampUpload;
	}

	@Nullable
	public JaxAuthLoginElement getUserUploaded() {
		return userUploaded;
	}

	public void setUserUploaded(@Nullable JaxAuthLoginElement userUploaded) {
		this.userUploaded = userUploaded;
	}
}
