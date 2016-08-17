package ch.dvbern.ebegu.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.UUID;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von TempDokument in der Datenbank.
 */
@Entity
public class TempDokument extends AbstractEntity {

	private static final long serialVersionUID = 5960979521430438226L;

	@Column(length = 36, nullable = false, updatable = false)
	private final String accessToken;

	@Column(length = 45, nullable = false, updatable = false)
	private final String ip;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_tempdokument_dokument_id"), nullable = false)
	//@OnDelete(action = OnDeleteAction.CASCADE)
	private Dokument dokument;

	public TempDokument() {
		this(new Dokument(), "");
	}

	public TempDokument(@Nonnull Dokument dokument, @Nonnull String ip) {
		this.accessToken = UUID.randomUUID().toString();
		this.dokument = dokument;
		this.ip = ip;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Dokument getDokument() {
		return dokument;
	}

	public void setDokument(Dokument dokument) {
		this.dokument = dokument;
	}

	public String getIp() {
		return ip;
	}
}
