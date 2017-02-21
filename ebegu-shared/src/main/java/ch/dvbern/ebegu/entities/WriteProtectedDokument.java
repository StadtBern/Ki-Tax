package ch.dvbern.ebegu.entities;

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
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WriteProtectedDokument extends File {

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
