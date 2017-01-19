package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitaet zum Speichern von GeneratedDokument in der Datenbank.
 */
@Audited
@Entity
/*@EntityListeners({GeneratedDokumentListener.class})*/
public class GeneratedDokument extends File {

	private static final long serialVersionUID = -895840426576485097L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private GeneratedDokumentTyp typ;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_generated_dokument_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	@Column(nullable = false)
	private boolean writeProtected = false;

	@Transient
	private boolean orginalWriteProtected;

	public GeneratedDokument() {
	}

	public GeneratedDokumentTyp getTyp() {
		return typ;
	}

	public void setTyp(GeneratedDokumentTyp typ) {
		this.typ = typ;
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	public boolean isWriteProtected() {
		return writeProtected;
	}

	public void setWriteProtected(boolean writeProtected) {
		this.writeProtected = writeProtected;
	}

	public boolean isOrginalWriteProtected() {
		return orginalWriteProtected;
	}

	public void setOrginalWriteProtected(boolean orginalWriteProtected) {
		this.orginalWriteProtected = orginalWriteProtected;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("typ", typ)
			.append("gesuch", gesuch)
			.toString();
	}
}
