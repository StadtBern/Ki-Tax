package ch.dvbern.ebegu.entities;

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

/**
 * Verfuegung fuer eine einzelne Betreuung
 */
@Entity
@Audited
public class Verfuegung extends AbstractEntity{

	private static final long serialVersionUID = -6682874795746487562L;


	@Transient
	private boolean sameVerfuegungsdaten;

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


	public String getGeneratedBemerkungen() {
		return generatedBemerkungen;
	}

	public void setGeneratedBemerkungen(String automatischeInitialisiertteBemerkungen) {
		this.generatedBemerkungen = automatischeInitialisiertteBemerkungen;
	}

	public String getManuelleBemerkungen() {
		return manuelleBemerkungen;
	}

	public void setManuelleBemerkungen(String manuelleBemerkungen) {
		this.manuelleBemerkungen = manuelleBemerkungen;
	}

	public List<VerfuegungZeitabschnitt> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(List<VerfuegungZeitabschnitt> zeitabschnitte) {
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

	public boolean isSameVerfuegungsdaten() {
		return sameVerfuegungsdaten;
	}

	public void setSameVerfuegungsdaten(boolean sameVerfuegungsdaten) {
		this.sameVerfuegungsdaten = sameVerfuegungsdaten;
	}
}
