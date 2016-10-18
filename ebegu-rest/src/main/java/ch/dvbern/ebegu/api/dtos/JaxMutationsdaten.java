package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Mutationsdaten
 */
@XmlRootElement(name = "mutationsdaten")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxMutationsdaten extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217319401394130097L;

	@Nullable
	private Boolean mutationFamiliensituation;

	@Nullable
	private Boolean mutationGesuchsteller;

	@Nullable
	private Boolean mutationUmzug;

	@Nullable
	private Boolean mutationKind;

	@Nullable
	private Boolean mutationBetreuung;

	@Nullable
	private Boolean mutationAbwesenheit;

	@Nullable
	private Boolean mutationErwerbspensum;

	@Nullable
	private Boolean mutationFinanzielleSituation;

	@Nullable
	private Boolean mutationEinkommensverschlechterung;

	@Nullable
	public Boolean getMutationFamiliensituation() {
		return mutationFamiliensituation;
	}

	public void setMutationFamiliensituation(@Nullable Boolean mutationFamiliensituation) {
		this.mutationFamiliensituation = mutationFamiliensituation;
	}

	@Nullable
	public Boolean getMutationGesuchsteller() {
		return mutationGesuchsteller;
	}

	public void setMutationGesuchsteller(@Nullable Boolean mutationGesuchsteller) {
		this.mutationGesuchsteller = mutationGesuchsteller;
	}

	@Nullable
	public Boolean getMutationUmzug() {
		return mutationUmzug;
	}

	public void setMutationUmzug(@Nullable Boolean mutationUmzug) {
		this.mutationUmzug = mutationUmzug;
	}

	@Nullable
	public Boolean getMutationKind() {
		return mutationKind;
	}

	public void setMutationKind(@Nullable Boolean mutationKind) {
		this.mutationKind = mutationKind;
	}

	@Nullable
	public Boolean getMutationBetreuung() {
		return mutationBetreuung;
	}

	public void setMutationBetreuung(@Nullable Boolean mutationBetreuung) {
		this.mutationBetreuung = mutationBetreuung;
	}

	@Nullable
	public Boolean getMutationAbwesenheit() {
		return mutationAbwesenheit;
	}

	public void setMutationAbwesenheit(@Nullable Boolean mutationAbwesenheit) {
		this.mutationAbwesenheit = mutationAbwesenheit;
	}

	@Nullable
	public Boolean getMutationErwerbspensum() {
		return mutationErwerbspensum;
	}

	public void setMutationErwerbspensum(@Nullable Boolean mutationErwerbspensum) {
		this.mutationErwerbspensum = mutationErwerbspensum;
	}

	@Nullable
	public Boolean getMutationFinanzielleSituation() {
		return mutationFinanzielleSituation;
	}

	public void setMutationFinanzielleSituation(@Nullable Boolean mutationFinanzielleSituation) {
		this.mutationFinanzielleSituation = mutationFinanzielleSituation;
	}

	@Nullable
	public Boolean getMutationEinkommensverschlechterung() {
		return mutationEinkommensverschlechterung;
	}

	public void setMutationEinkommensverschlechterung(@Nullable Boolean mutationEinkommensverschlechterung) {
		this.mutationEinkommensverschlechterung = mutationEinkommensverschlechterung;
	}
}
