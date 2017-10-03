package ch.dvbern.ebegu.entities;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.enums.GeneratedDokumentTyp;
import ch.dvbern.ebegu.util.Constants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von GeneratedDokument in der Datenbank.
 */
@Audited
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WriteProtectedDokument extends FileMetadata {

	private static final long serialVersionUID = 1119768378567437676L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private GeneratedDokumentTyp typ;

	@Column(nullable = false)
	private boolean writeProtected = false;

	@Transient
	private boolean orginalWriteProtected;

	public WriteProtectedDokument() {
	}

	public GeneratedDokumentTyp getTyp() {
		return typ;
	}

	public void setTyp(GeneratedDokumentTyp typ) {
		this.typ = typ;
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
			.toString();
	}
}
