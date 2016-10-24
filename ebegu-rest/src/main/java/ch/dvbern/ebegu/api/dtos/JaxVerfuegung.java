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
}
