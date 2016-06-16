package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstrakte Entitaet. Muss von Entitaeten erweitert werden, die einen Antrag sind z.B. Gesuch und Mutation
 */
@MappedSuperclass
@Audited
public class AbstractAntragEntity extends AbstractEntity {

	private static final long serialVersionUID = -8203487739884704615L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuch_fall_id"))
	private Fall fall;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_antrag_gesuchsperiode_id"))
	private Gesuchsperiode gesuchsperiode;

	@Nonnull
	@NotNull
	@Column(nullable = false)
	private LocalDate eingangsdatum;

	public Fall getFall() {
		return fall;
	}

	public void setFall(Fall fall) {
		this.fall = fall;
	}

	public Gesuchsperiode getGesuchsperiode() {
		return gesuchsperiode;
	}

	public void setGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		this.gesuchsperiode = gesuchsperiode;
	}

	@Nonnull
	public LocalDate getEingangsdatum() {
		return eingangsdatum;
	}

	public void setEingangsdatum(@Nonnull LocalDate eingangsdatum) {
		this.eingangsdatum = eingangsdatum;
	}

	@SuppressWarnings("ObjectEquality")
	public boolean isSame(AbstractAntragEntity otherAbstAntragEntity) {
		if (this == otherAbstAntragEntity) {
			return true;
		}
		if (otherAbstAntragEntity == null || getClass() != otherAbstAntragEntity.getClass()) {
			return false;
		}
		return (Objects.equals(this.getEingangsdatum(), otherAbstAntragEntity.getEingangsdatum())
			&& Objects.equals(this.getFall(), otherAbstAntragEntity.getFall())
			&& Objects.equals(this.getGesuchsperiode(), otherAbstAntragEntity.getGesuchsperiode()));
	}
}
