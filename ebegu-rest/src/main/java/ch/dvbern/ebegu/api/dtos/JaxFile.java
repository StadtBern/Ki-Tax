package ch.dvbern.ebegu.api.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "file")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxFile extends JaxAbstractDTO {

	private static final long serialVersionUID = 1118235796540488553L;

	private String fileName;

	private String filePfad;

	private String fileSize;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePfad() {
		return filePfad;
	}

	public void setFilePfad(String filePfad) {
		this.filePfad = filePfad;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		JaxFile that = (JaxFile) o;

		if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) {
			return false;
		}
		if (filePfad != null ? !filePfad.equals(that.filePfad) : that.filePfad != null) {
			return false;
		}
		return fileSize != null ? fileSize.equals(that.fileSize) : that.fileSize == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
		result = 31 * result + (filePfad != null ? filePfad.hashCode() : 0);
		result = 31 * result + (fileSize != null ? fileSize.hashCode() : 0);
		return result;
	}
}
