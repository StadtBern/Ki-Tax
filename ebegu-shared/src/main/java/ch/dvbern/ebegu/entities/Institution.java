package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Entitaet zum Speichern von Institution in der Datenbank.
 */
@Audited
@Entity
@AssociationOverrides({
   @AssociationOverride(name = "mandant",
	   //wird von hibernate 5.0.6 ignoriert... https://hibernate.atlassian.net/browse/HHH-10387
      joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_institution_mandant_id")))
})
public class Institution extends AbstractMandantEntity {

	private static final long serialVersionUID = -8706487439884760618L;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String name;

	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_institution_traegerschaft_id"))
	private Traegerschaft traegerschaft;

	@NotNull
	@Column(nullable = false)
	private Boolean active = true;

	public Institution() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Traegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(Traegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}



	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}


}
