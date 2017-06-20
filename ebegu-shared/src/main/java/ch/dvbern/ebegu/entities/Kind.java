package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Kinderabzug;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entity fuer Kinder.
 */
@Audited
@Entity
@Table(
	indexes = {
		@Index(columnList = "geburtsdatum", name = "IX_kind_geburtsdatum")
	})
public class Kind extends AbstractPersonEntity {

	private static final long serialVersionUID = -9032257320578372570L;

	@Max(100)
	@Min(0)
	@Nullable
	@Column(nullable = true)
	private Integer wohnhaftImGleichenHaushalt;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Kinderabzug kinderabzug;

	@Column(nullable = false)
	@NotNull
	private Boolean familienErgaenzendeBetreuung = false;

	@Column(nullable = true)
	@Nullable
	private Boolean mutterspracheDeutsch;

	@Column(nullable = true)
	@Nullable
	private Boolean einschulung;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_kind_pensum_fachstelle_id"), nullable = true)
	private PensumFachstelle pensumFachstelle;


	public Kind() {
	}


	@Nullable
	public Integer getWohnhaftImGleichenHaushalt() {
		return wohnhaftImGleichenHaushalt;
	}

	public void setWohnhaftImGleichenHaushalt(@Nullable Integer wohnhaftImGleichenHaushalt) {
		this.wohnhaftImGleichenHaushalt = wohnhaftImGleichenHaushalt;
	}

	public Kinderabzug getKinderabzug() {
		return kinderabzug;
	}

	public void setKinderabzug(Kinderabzug kinderabzug) {
		this.kinderabzug = kinderabzug;
	}

	public Boolean getFamilienErgaenzendeBetreuung() {
		return familienErgaenzendeBetreuung;
	}

	public void setFamilienErgaenzendeBetreuung(Boolean familienErgaenzendeBetreuung) {
		this.familienErgaenzendeBetreuung = familienErgaenzendeBetreuung;
	}

	@Nullable
	public Boolean getMutterspracheDeutsch() {
		return mutterspracheDeutsch;
	}

	public void setMutterspracheDeutsch(@Nullable Boolean mutterspracheDeutsch) {
		this.mutterspracheDeutsch = mutterspracheDeutsch;
	}

	public PensumFachstelle getPensumFachstelle() {
		return pensumFachstelle;
	}

	public void setPensumFachstelle(PensumFachstelle pensumFachstelle) {
		this.pensumFachstelle = pensumFachstelle;
	}

	@Nullable
	public Boolean getEinschulung() {
		return einschulung;
	}

	public void setEinschulung(@Nullable Boolean einschulung) {
		this.einschulung = einschulung;
	}

	@Nonnull
	public Kind copyForMutation(@Nonnull Kind mutation) {
		super.copyForMutation(mutation);
		if (this.getPensumFachstelle() != null) {
			mutation.setPensumFachstelle(this.getPensumFachstelle().copyForMutation(new PensumFachstelle()));
		}
		return copyForMutationOrErneuerung(mutation);
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	@Nonnull
	public Kind copyForErneuerung(@Nonnull Kind folgegesuchKind, @Nonnull Gesuchsperiode gesuchsperiodeFolgegesuch) {
		super.copyForErneuerung(folgegesuchKind);
		if (this.getPensumFachstelle() != null) {
			// Fachstelle nur kopieren, wenn sie noch gueltig ist
			if (!this.getPensumFachstelle().getGueltigkeit().endsBefore(gesuchsperiodeFolgegesuch.getGueltigkeit().getGueltigAb())) {
				folgegesuchKind.setPensumFachstelle(this.getPensumFachstelle().copyForErneuerung(new PensumFachstelle()));
			}
		}
		return copyForMutationOrErneuerung(folgegesuchKind);
	}

    @Nonnull
	private Kind copyForMutationOrErneuerung(@Nonnull Kind mutation) {
		mutation.setWohnhaftImGleichenHaushalt(this.getWohnhaftImGleichenHaushalt());
		mutation.setKinderabzug(this.getKinderabzug());
		mutation.setFamilienErgaenzendeBetreuung(this.getFamilienErgaenzendeBetreuung());
		mutation.setMutterspracheDeutsch(this.getMutterspracheDeutsch());
		mutation.setEinschulung(this.getEinschulung());
		return mutation;
	}

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!super.isSame(other)) {
			return false;
		}
		final Kind otherKind = (Kind) other;
		return Objects.equals(getWohnhaftImGleichenHaushalt(), otherKind.getWohnhaftImGleichenHaushalt()) &&
			getKinderabzug() == otherKind.getKinderabzug() &&
			Objects.equals(getFamilienErgaenzendeBetreuung(), otherKind.getFamilienErgaenzendeBetreuung()) &&
			Objects.equals(getMutterspracheDeutsch(), otherKind.getMutterspracheDeutsch()) &&
			Objects.equals(getEinschulung(), otherKind.getEinschulung()) &&
			Objects.equals(getPensumFachstelle(), otherKind.getPensumFachstelle());
	}
}
