package ch.dvbern.ebegu.entities;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Entitaet zum Speichern von Mutationsdaten in der Datenbank.
 */
@Audited
@Entity
public class Mutationsdaten extends AbstractEntity {

	private static final long serialVersionUID = -8403487439819600618L;


//	@OneToOne(optional = false, mappedBy = "mutationsdaten")
//	private Gesuch gesuch;

	@Column(nullable = true)
	private Boolean mutationFamiliensituation;

	@Column(nullable = true)
	private Boolean mutationGesuchsteller;

	@Column(nullable = true)
	private Boolean mutationUmzug;

	@Column(nullable = true)
	private Boolean mutationKind;

	@Column(nullable = true)
	private Boolean mutationBetreuung;

	@Column(nullable = true)
	private Boolean mutationAbwesenheit;

	@Column(nullable = true)
	private Boolean mutationErwerbspensum;

	@Column(nullable = true)
	private Boolean mutationFinanzielleSituation;

	@Column(nullable = true)
	private Boolean mutationEinkommensverschlechterung;


	public Mutationsdaten() {}

	public Mutationsdaten(@NotNull Mutationsdaten toCopy, final Gesuch gesuch) {
//		this.setGesuch(gesuch);
		this.setMutationFamiliensituation(toCopy.getMutationFamiliensituation());
		this.setMutationGesuchsteller(toCopy.getMutationGesuchsteller());
		this.setMutationUmzug(toCopy.getMutationUmzug());
		this.setMutationKind(toCopy.getMutationKind());
		this.setMutationBetreuung(toCopy.getMutationBetreuung());
		this.setMutationAbwesenheit(toCopy.getMutationAbwesenheit());
		this.setMutationErwerbspensum(toCopy.getMutationErwerbspensum());
		this.setMutationFinanzielleSituation(toCopy.getMutationFinanzielleSituation());
		this.setMutationEinkommensverschlechterung(toCopy.getMutationEinkommensverschlechterung());
	}

//	public Gesuch getGesuch() {
//		return gesuch;
//	}
//
//	public final void setGesuch(Gesuch gesuch) {
//		this.gesuch = gesuch;
//	}

	public Boolean getMutationFamiliensituation() {
		return mutationFamiliensituation;
	}

	public final void setMutationFamiliensituation(Boolean mutationFamiliensituation) {
		this.mutationFamiliensituation = mutationFamiliensituation;
	}

	public Boolean getMutationGesuchsteller() {
		return mutationGesuchsteller;
	}

	public final void setMutationGesuchsteller(Boolean mutationGesuchsteller) {
		this.mutationGesuchsteller = mutationGesuchsteller;
	}

	public Boolean getMutationUmzug() {
		return mutationUmzug;
	}

	public final void setMutationUmzug(Boolean mutationUmzug) {
		this.mutationUmzug = mutationUmzug;
	}

	public Boolean getMutationKind() {
		return mutationKind;
	}

	public final void setMutationKind(Boolean mutationKind) {
		this.mutationKind = mutationKind;
	}

	public Boolean getMutationBetreuung() {
		return mutationBetreuung;
	}

	public final void setMutationBetreuung(Boolean mutationBetreuung) {
		this.mutationBetreuung = mutationBetreuung;
	}

	public Boolean getMutationAbwesenheit() {
		return mutationAbwesenheit;
	}

	public final void setMutationAbwesenheit(Boolean mutationAbwesenheit) {
		this.mutationAbwesenheit = mutationAbwesenheit;
	}

	public Boolean getMutationErwerbspensum() {
		return mutationErwerbspensum;
	}

	public final void setMutationErwerbspensum(Boolean mutationErwerbspensum) {
		this.mutationErwerbspensum = mutationErwerbspensum;
	}

	public Boolean getMutationFinanzielleSituation() {
		return mutationFinanzielleSituation;
	}

	public final void setMutationFinanzielleSituation(Boolean mutationFinanzielleSituation) {
		this.mutationFinanzielleSituation = mutationFinanzielleSituation;
	}

	public Boolean getMutationEinkommensverschlechterung() {
		return mutationEinkommensverschlechterung;
	}

	public final void setMutationEinkommensverschlechterung(Boolean mutationEinkommensverschlechterung) {
		this.mutationEinkommensverschlechterung = mutationEinkommensverschlechterung;
	}
}
