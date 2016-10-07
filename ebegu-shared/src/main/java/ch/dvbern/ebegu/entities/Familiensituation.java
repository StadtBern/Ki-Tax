package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entitaet zum Speichern von Familiensituation in der Datenbank.
 */
@Audited
@Entity
public class Familiensituation extends AbstractEntity {

	private static final long serialVersionUID = -6534582356181164632L;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private EnumFamilienstatus familienstatus;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	@Column(nullable = true)
	private EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet;

	@Column(nullable = true)
	private Boolean gemeinsameSteuererklaerung;


	public Familiensituation() {
	}

	public Familiensituation(Familiensituation that) {
		this.familienstatus = that.getFamilienstatus();
		this.gemeinsameSteuererklaerung = that.getGemeinsameSteuererklaerung();
		this.gesuchstellerKardinalitaet = that.getGesuchstellerKardinalitaet();
		this.setId(that.getId());
		this.setTimestampErstellt(that.getTimestampErstellt());
		this.setTimestampMutiert(that.getTimestampMutiert());
		this.setUserErstellt(that.getUserErstellt());
		this.setUserMutiert(that.getUserMutiert());
		this.setVersion(that.getVersion());
	}

	@Nonnull
	public EnumFamilienstatus getFamilienstatus() {
		return familienstatus;
	}

	public void setFamilienstatus(@Nonnull EnumFamilienstatus familienstatus) {
		this.familienstatus = familienstatus;
	}

	@Nullable
	public EnumGesuchstellerKardinalitaet getGesuchstellerKardinalitaet() {
		return gesuchstellerKardinalitaet;
	}

	public void setGesuchstellerKardinalitaet(@Nullable EnumGesuchstellerKardinalitaet gesuchstellerKardinalitaet) {
		this.gesuchstellerKardinalitaet = gesuchstellerKardinalitaet;
	}

	public Boolean getGemeinsameSteuererklaerung() {
		return gemeinsameSteuererklaerung;
	}

	public void setGemeinsameSteuererklaerung(Boolean gemeinsameSteuererklaerung) {
		this.gemeinsameSteuererklaerung = gemeinsameSteuererklaerung;
	}

	@Transient
	public boolean hasSecondGesuchsteller() {
		if (this.familienstatus != null) {
			switch (this.familienstatus){
				case ALLEINERZIEHEND:
				case WENIGER_FUENF_JAHRE:
					return EnumGesuchstellerKardinalitaet.ZU_ZWEIT.equals(this.getGesuchstellerKardinalitaet());
				case VERHEIRATET:
				case KONKUBINAT:
				case LAENGER_FUENF_JAHRE:
					return true;
			}
		}
		return false;
	}
}
