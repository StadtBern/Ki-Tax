package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.enums.Geschlecht;
import ch.dvbern.ebegu.util.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

	private Collection<JaxErwerbspensumContainer> erwerbspensenContainers = new HashSet<>();


	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getTelefonAusland() {
		return telefonAusland;
	}

	public void setTelefonAusland(String telefonAusland) {
		this.telefonAusland = telefonAusland;
	}

	public String getZpvNumber() {
		return zpvNumber;
	}

	public void setZpvNumber(String zpvNumber) {
		this.zpvNumber = zpvNumber;
	}

	public JaxAdresse getWohnAdresse() {
		return wohnAdresse;
	}

	public void setWohnAdresse(JaxAdresse wohnAdresse) {
		this.wohnAdresse = wohnAdresse;
	}

	public JaxAdresse getAlternativeAdresse() {
		return alternativeAdresse;
	}

	public void setAlternativeAdresse(JaxAdresse alternativeAdresse) {
		this.alternativeAdresse = alternativeAdresse;
	}

	public JaxAdresse getUmzugAdresse() {
		return umzugAdresse;
	}

	public void setUmzugAdresse(JaxAdresse umzugAdresse) {
		this.umzugAdresse = umzugAdresse;
	}

	public JaxFinanzielleSituationContainer getFinanzielleSituationContainer() {
		return finanzielleSituationContainer;
	}

	public void setFinanzielleSituationContainer(JaxFinanzielleSituationContainer finanzielleSituationContainer) {
		this.finanzielleSituationContainer = finanzielleSituationContainer;
	}

	public Collection<JaxErwerbspensumContainer> getErwerbspensenContainers() {
		return erwerbspensenContainers;
	}

	public void setErwerbspensenContainers(Collection<JaxErwerbspensumContainer> erwerbspensenContainers) {
		this.erwerbspensenContainers = erwerbspensenContainers;
	}
}
