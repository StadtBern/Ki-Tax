package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.LinkedHashSet;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * DTO fuer Stammdaten der Gesuchsteller (kennt adresse)
 */
@XmlRootElement(name = "gesuchsteller")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchsteller extends JaxAbstractPersonDTO {

	private static final long serialVersionUID = -1297026901664130397L;

	@Pattern(regexp = Constants.REGEX_EMAIL, message = "{validator.constraints.Email.message}")
	@Size(min = 5, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	private String mail;

	@Pattern(regexp = Constants.REGEX_TELEFON_MOBILE, message = "{error_invalid_mobilenummer}")
	private String mobile;

	@Pattern(regexp = Constants.REGEX_TELEFON, message = "{error_invalid_mobilenummer}")
	private String telefon;

	private String telefonAusland;

	private String zpvNumber; //todo team, es ist noch offen was das genau fuer ein identifier ist

	//Adressen
	@NotNull
	@Valid
	private JaxAdresse wohnAdresse;

	@Valid
	private JaxAdresse alternativeAdresse;

	@Valid
	private JaxAdresse umzugAdresse;

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainer;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer;

	private Collection<JaxErwerbspensumContainer> erwerbspensenContainers = new LinkedHashSet<>();

	private boolean diplomatenstatus;


	public String getMail() {
		return mail;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(final String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(final String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getZpvNumber() {
		return zpvNumber;
	}

	public void setZpvNumber(final String zpvNumber) {
		this.zpvNumber = zpvNumber;
	}

	public JaxAdresse getWohnAdresse() {
		return wohnAdresse;
	}

	public void setWohnAdresse(final JaxAdresse wohnAdresse) {
		this.wohnAdresse = wohnAdresse;
	}

	public JaxAdresse getAlternativeAdresse() {
		return alternativeAdresse;
	}

	public void setAlternativeAdresse(final JaxAdresse alternativeAdresse) {
		this.alternativeAdresse = alternativeAdresse;
	}

	public JaxAdresse getUmzugAdresse() {
		return umzugAdresse;
	}

	public void setUmzugAdresse(final JaxAdresse umzugAdresse) {
		this.umzugAdresse = umzugAdresse;
	}

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	public void setFinanzielleSituationContainer(final JaxFinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
	}

	public Collection<JaxErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	public void setErwerbspensenContainers(final Collection<JaxErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}

	public boolean isDiplomatenstatus() {
		return diplomatenstatus;
	}

	public void setDiplomatenstatus(final boolean diplomatenstatus) {
		this.diplomatenstatus = diplomatenstatus;
	}

	@Nullable
	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		return einkommensverschlechterungContainer;
	}

	public void setEinkommensverschlechterungContainer(@Nullable JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		this.einkommensverschlechterungContainer = einkommensverschlechterungContainer;
	}
}
