package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Verfuegung fuer eine einzelne Betreuung
 */
@Entity
@Audited
public class Verfuegung extends AbstractEntity{

	private static final long serialVersionUID = -6682874795746487562L;


	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String generatedBemerkungen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String manuelleBemerkungen;

	@NotNull
	@OneToOne (optional = false, mappedBy = "verfuegung")
	private Betreuung betreuung;

	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "verfuegung")
	@OrderBy("gueltigkeit ASC")
	private List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();

	@NotNull
	@Column(nullable = false)
	private boolean kategorieNormal = false;

	@NotNull
	@Column(nullable = false)
	private boolean kategorieMaxEinkommen = false;

	@NotNull
	@Column(nullable = false)
	private boolean kategorieKeinPensum = false;

	@NotNull
	@Column(nullable = false)
	private boolean kategorieZuschlagZumErwerbspensum = false;

	@NotNull
	@Column(nullable = false)
	private boolean kategorieNichtEintreten = false;


	@Nullable
	public String getGeneratedBemerkungen() {
		return generatedBemerkungen;
	}

	public void setGeneratedBemerkungen(@Nullable String autoInitialisierteBemerkungen) {
		this.generatedBemerkungen = autoInitialisierteBemerkungen;
	}

	@Nullable
	public String getManuelleBemerkungen() {
		return manuelleBemerkungen;
	}

	public void setManuelleBemerkungen(@Nullable String manuelleBemerkungen) {
		this.manuelleBemerkungen = manuelleBemerkungen;
	}

	@Nonnull
	public List<VerfuegungZeitabschnitt> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(@Nonnull List<VerfuegungZeitabschnitt> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
		for (VerfuegungZeitabschnitt zeitabschnitt : this.zeitabschnitte) {
			zeitabschnitt.setVerfuegung(this);
		}
	}

	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public boolean isKategorieNormal() {
		return kategorieNormal;
	}

	public void setKategorieNormal(boolean kategorieNormal) {
		this.kategorieNormal = kategorieNormal;
	}

	public boolean isKategorieMaxEinkommen() {
		return kategorieMaxEinkommen;
	}

	public void setKategorieMaxEinkommen(boolean kategorieMaxEinkommen) {
		this.kategorieMaxEinkommen = kategorieMaxEinkommen;
	}

	public boolean isKategorieKeinPensum() {
		return kategorieKeinPensum;
	}

	public void setKategorieKeinPensum(boolean kategorieKeinPensum) {
		this.kategorieKeinPensum = kategorieKeinPensum;
	}

	public boolean isKategorieZuschlagZumErwerbspensum() {
		return kategorieZuschlagZumErwerbspensum;
	}

	public void setKategorieZuschlagZumErwerbspensum(boolean kategorieZuschlagZumErwerbspensum) {
		this.kategorieZuschlagZumErwerbspensum = kategorieZuschlagZumErwerbspensum;
	}

	public boolean isKategorieNichtEintreten() {
		return kategorieNichtEintreten;
	}

	public void setKategorieNichtEintreten(boolean kategorieNichtEintreten) {
		this.kategorieNichtEintreten = kategorieNichtEintreten;
	}

	public boolean addZeitabschnitt(@NotNull final VerfuegungZeitabschnitt zeitabschnitt) {
		zeitabschnitt.setVerfuegung(this);
		return !this.zeitabschnitte.add(zeitabschnitt);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Verfuegung");
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			sb.append("\n");
			sb.append(zeitabschnitt);
		}
		return sb.toString();
	}

	public String toStringFinanzielleSituation() {
		StringBuilder sb = new StringBuilder("Verfuegung");
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			sb.append("\n");
			sb.append(zeitabschnitt.toStringFinanzielleSituation());
		}
		return sb.toString();
	}

	// todo imanol -> move method to a utils????
	public void setIsSameVerfuegungsdaten() {
		final Verfuegung verfuegungOnGesuchForMutation = betreuung.getVorgaengerVerfuegung();
		if (verfuegungOnGesuchForMutation != null) {
			final List<VerfuegungZeitabschnitt> newZeitabschnitte = this.getZeitabschnitte();
			final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = verfuegungOnGesuchForMutation.getZeitabschnitte();

			for (VerfuegungZeitabschnitt newZeitabschnitt : newZeitabschnitte) {
				// todo imanol Dies sollte auch subzeitabschnitte vergleichen
				Optional<VerfuegungZeitabschnitt> oldSameZeitabschnitt = findZeitabschnittSameGueltigkeit(zeitabschnitteGSM, newZeitabschnitt);
				if (oldSameZeitabschnitt.isPresent()) {
					newZeitabschnitt.setSameVerfuegungsdaten(newZeitabschnitt.isSamePersistedValues(oldSameZeitabschnitt.get()));
				}
				else { // no Zeitabschnitt with the same Gueltigkeit has been found, so it must be different
					newZeitabschnitt.setSameVerfuegungsdaten(false);
				}
			}
		}
	}

	private Optional<VerfuegungZeitabschnitt> findZeitabschnittSameGueltigkeit(List<VerfuegungZeitabschnitt> zeitabschnitteGSM, VerfuegungZeitabschnitt newZeitabschnitt) {
		for (VerfuegungZeitabschnitt zeitabschnittGSM : zeitabschnitteGSM) {
			if (zeitabschnittGSM.getGueltigkeit().equals(newZeitabschnitt.getGueltigkeit())) {
				return Optional.of(zeitabschnittGSM);
			}
		}
		return Optional.empty();
	}

	// todo imanol -> move method to a utils????
	public void setZahlungsstatus() {
		final Verfuegung verfuegungOnGesuchForMutation = betreuung.getVorgaengerVerfuegung();
		if (verfuegungOnGesuchForMutation != null) {
			final List<VerfuegungZeitabschnitt> newZeitabschnitte = this.getZeitabschnitte();
			final List<VerfuegungZeitabschnitt> zeitabschnitteGSM = verfuegungOnGesuchForMutation.getZeitabschnitte();

			for (VerfuegungZeitabschnitt newZeitabschnitt : newZeitabschnitte) {
				VerfuegungsZeitabschnittZahlungsstatus oldStatusZeitabschnitt = findStatusOldZeitabschnitt(zeitabschnitteGSM, newZeitabschnitt);
				newZeitabschnitt.setZahlungsstatus(oldStatusZeitabschnitt);
			}
		}
	}

	private VerfuegungsZeitabschnittZahlungsstatus findStatusOldZeitabschnitt(List<VerfuegungZeitabschnitt> zeitabschnitteGSM, VerfuegungZeitabschnitt newZeitabschnitt) {
		for (VerfuegungZeitabschnitt zeitabschnittGSM : zeitabschnitteGSM) {
			if (zeitabschnittGSM.getGueltigkeit().getOverlap(newZeitabschnitt.getGueltigkeit()).isPresent()) {
				// wir gehen davon aus, dass Zahlung immer fuer einen ganzen Monat gemacht werden, deswegen reicht es wenn ein Zeitabschnitt VERRECHNET bzw. IGNORIERT ist
				if (zeitabschnittGSM.getZahlungsstatus().equals(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET)) {
					return VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET;
				}
				else if (zeitabschnittGSM.getZahlungsstatus().equals(VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT)) {
					return VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT;
				}
			}
		}
		return VerfuegungsZeitabschnittZahlungsstatus.NEU;
	}
}
