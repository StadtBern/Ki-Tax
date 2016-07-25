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

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String generatedBemerkungen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	@Column(nullable = true, length = Constants.DB_TEXTAREA_LENGTH)
	private String manuelleBemerkungen;

	@NotNull
	@OneToOne (optional = false, mappedBy = "verfuegung", cascade = CascadeType.ALL, orphanRemoval = true )
	private Betreuung betreuung;


	@Nonnull
	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "verfuegung")
	private List<VerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();


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
	}

	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Verfuegung");
		for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnitte) {
			sb.append("\n");
			sb.append(zeitabschnitt);
		}
		return sb.toString();
	}
}
