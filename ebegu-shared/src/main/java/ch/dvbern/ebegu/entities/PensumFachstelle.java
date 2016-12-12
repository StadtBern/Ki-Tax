package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity fuer PensumFachstelle.
 */
@Audited
@Entity
public class PensumFachstelle extends AbstractPensumEntity {

	private static final long serialVersionUID = -9132257320978374570L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_pensum_fachstelle_fachstelle_id"))
	private Fachstelle fachstelle;


	public PensumFachstelle() {
	}


	public Fachstelle getFachstelle() {
		return fachstelle;
	}

	public void setFachstelle(Fachstelle fachstelle) {
		this.fachstelle = fachstelle;
	}

	public PensumFachstelle copyForMutation(PensumFachstelle mutation) {
		mutation.setFachstelle(this.getFachstelle());
		return mutation;
	}
}
