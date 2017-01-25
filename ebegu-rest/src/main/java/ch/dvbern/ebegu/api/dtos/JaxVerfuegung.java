package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO fuer Verfuegungen
 */
@XmlRootElement(name = "verfuegung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxVerfuegung extends JaxAbstractDTO {

	private static final long serialVersionUID = 3359889270785929022L;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String generatedBemerkungen;

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String manuelleBemerkungen;

	@Nonnull
	private List<JaxVerfuegungZeitabschnitt> zeitabschnitte = new ArrayList<>();

	private boolean sameVerfuegungsdaten;

	private boolean kategorieNormal = false;

	private boolean kategorieMaxEinkommen = false;

	private boolean kategorieKeinPensum = false;

	private boolean kategorieZuschlagZumErwerbspensum = false;

	private boolean kategorieNichtEintreten = false;


	@Nullable
	public String getGeneratedBemerkungen() {
		return generatedBemerkungen;
	}

	public void setGeneratedBemerkungen(@Nullable String generatedBemerkungen) {
		this.generatedBemerkungen = generatedBemerkungen;
	}

	@Nullable
	public String getManuelleBemerkungen() {
		return manuelleBemerkungen;
	}

	public void setManuelleBemerkungen(@Nullable String manuelleBemerkungen) {
		this.manuelleBemerkungen = manuelleBemerkungen;
	}

	@Nonnull
	public List<JaxVerfuegungZeitabschnitt> getZeitabschnitte() {
		return zeitabschnitte;
	}

	public void setZeitabschnitte(@Nonnull List<JaxVerfuegungZeitabschnitt> zeitabschnitte) {
		this.zeitabschnitte = zeitabschnitte;
	}

	public boolean isSameVerfuegungsdaten() {
		return sameVerfuegungsdaten;
	}

	public void setSameVerfuegungsdaten(boolean sameVerfuegungsdaten) {
		this.sameVerfuegungsdaten = sameVerfuegungsdaten;
	}

	@Nonnull
	public boolean isKategorieNormal() {
		return kategorieNormal;
	}

	public void setKategorieNormal(@Nonnull boolean kategorieNormal) {
		this.kategorieNormal = kategorieNormal;
	}

	@Nonnull
	public boolean isKategorieMaxEinkommen() {
		return kategorieMaxEinkommen;
	}

	public void setKategorieMaxEinkommen(@Nonnull boolean kategorieMaxEinkommen) {
		this.kategorieMaxEinkommen = kategorieMaxEinkommen;
	}

	@Nonnull
	public boolean isKategorieKeinPensum() {
		return kategorieKeinPensum;
	}

	public void setKategorieKeinPensum(@Nonnull boolean kategorieKeinPensum) {
		this.kategorieKeinPensum = kategorieKeinPensum;
	}

	@Nonnull
	public boolean isKategorieZuschlagZumErwerbspensum() {
		return kategorieZuschlagZumErwerbspensum;
	}

	public void setKategorieZuschlagZumErwerbspensum(@Nonnull boolean kategorieZuschlagZumErwerbspensum) {
		this.kategorieZuschlagZumErwerbspensum = kategorieZuschlagZumErwerbspensum;
	}

	@Nonnull
	public boolean isKategorieNichtEintreten() {
		return kategorieNichtEintreten;
	}

	public void setKategorieNichtEintreten(@Nonnull boolean kategorieNichtEintreten) {
		this.kategorieNichtEintreten = kategorieNichtEintreten;
	}
}
