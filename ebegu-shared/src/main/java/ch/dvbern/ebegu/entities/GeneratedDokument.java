package ch.dvbern.ebegu.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

/**
 * Entitaet zum Speichern von GeneratedDokument in der Datenbank.
 */
@Audited
@Entity
@EntityListeners({ WriteProtectedDokumentListener.class })
public class GeneratedDokument extends WriteProtectedDokument {

	private static final long serialVersionUID = -895840426576485097L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_generated_dokument_gesuch_id"), nullable = false)
	private Gesuch gesuch;

	public GeneratedDokument() {
	}

	public Gesuch getGesuch() {
		return gesuch;
	}

	public void setGesuch(Gesuch gesuch) {
		this.gesuch = gesuch;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.append("gesuch", gesuch)
			.toString();
	}
}
