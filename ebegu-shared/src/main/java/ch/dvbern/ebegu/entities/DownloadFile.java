package ch.dvbern.ebegu.entities;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

/**
 * Entitaet zum Speichern von DownloadFile in der Datenbank.
 */
@Entity
public class DownloadFile extends File {

	private static final long serialVersionUID = 5960979521430438226L;

	@Column(length = 36, nullable = false, updatable = false)
	private final String accessToken;

	@Column(length = 45, nullable = false, updatable = false)
	private final String ip;

	public DownloadFile() {
		this.accessToken = UUID.randomUUID().toString();
		this.ip = "";
	}

	public DownloadFile(@Nonnull File file, @Nonnull String ip) {
		super(file);
		this.accessToken = UUID.randomUUID().toString();
		this.ip = ip;

	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getIp() {
		return ip;
	}
}
