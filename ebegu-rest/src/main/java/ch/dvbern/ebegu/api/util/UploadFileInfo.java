package ch.dvbern.ebegu.api.util;

import javax.activation.MimeType;
import javax.annotation.Nullable;
import java.io.Serializable;

public class UploadFileInfo implements Serializable {
	private static final long serialVersionUID = -5794206656522186893L;

	@Nullable
	private String filename;
	@Nullable
	private final MimeType contentType;
	@Nullable
	byte[] bytes;

	public UploadFileInfo(@Nullable
							  String filename, @Nullable
							  MimeType contentType) {
		this.filename = filename;
		this.contentType = contentType;
	}

	@Nullable
	public String getFilename() {
		return filename;
	}

	public void setFilename(@Nullable String filename) {
		this.filename = filename;
	}

	@Nullable
	public MimeType getContentType() {
		return contentType;
	}

	@Nullable
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(@Nullable byte[] bytes) {
		this.bytes = bytes;
	}
}
