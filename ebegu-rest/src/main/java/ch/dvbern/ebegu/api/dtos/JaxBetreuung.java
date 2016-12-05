package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO fuer Daten der Betreuungen,
 */
@XmlRootElement(name = "betreuung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBetreuung extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297022381674937397L;

	@NotNull
	private JaxInstitutionStammdaten institutionStammdaten;

	@NotNull
	private Betreuungsstatus betreuungsstatus;

	@NotNull
	private List<JaxBetreuungspensumContainer> betreuungspensumContainers = new ArrayList<>();

	@NotNull
	private List<JaxAbwesenheitContainer> abwesenheitContainers = new ArrayList<>();

	@Size(max = Constants.DB_TEXTAREA_LENGTH)
	@Nullable
	private String grundAblehnung;

	@Min(1)
	private Integer betreuungNummer = 1;

	@Nullable
	private JaxVerfuegung verfuegung;

	@NotNull
	private Boolean vertrag;

	@NotNull
	private Boolean erweiterteBeduerfnisse;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumAblehnung = null;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumBestaetigung = null;


	public JaxInstitutionStammdaten getInstitutionStammdaten() {
		return institutionStammdaten;
	}

	public void setInstitutionStammdaten(JaxInstitutionStammdaten institutionStammdaten) {
		this.institutionStammdaten = institutionStammdaten;
	}

	public Betreuungsstatus getBetreuungsstatus() {
		return betreuungsstatus;
	}

	public void setBetreuungsstatus(Betreuungsstatus betreuungsstatus) {
		this.betreuungsstatus = betreuungsstatus;
	}

	public List<JaxBetreuungspensumContainer> getBetreuungspensumContainers() {
		return betreuungspensumContainers;
	}

	public void setBetreuungspensumContainers(List<JaxBetreuungspensumContainer> betreuungspensumContainers) {
		this.betreuungspensumContainers = betreuungspensumContainers;
	}

	public List<JaxAbwesenheitContainer> getAbwesenheitContainers() {
		return abwesenheitContainers;
	}

	public void setAbwesenheitContainers(List<JaxAbwesenheitContainer> abwesenheiten) {
		this.abwesenheitContainers = abwesenheiten;
	}

	@Nullable
	public String getGrundAblehnung() {
		return grundAblehnung;
	}

	public void setGrundAblehnung(@Nullable String grundAblehnung) {
		this.grundAblehnung = grundAblehnung;
	}

	public Integer getBetreuungNummer() {
		return betreuungNummer;
	}

	public void setBetreuungNummer(Integer betreuungNummer) {
		this.betreuungNummer = betreuungNummer;
	}

	@Nullable
	public JaxVerfuegung getVerfuegung() {
		return verfuegung;
	}

	public void setVerfuegung(@Nullable JaxVerfuegung verfuegung) {
		this.verfuegung = verfuegung;
	}

	public Boolean getVertrag() {
		return vertrag;
	}

	public void setVertrag(Boolean vertrag) {
		this.vertrag = vertrag;
	}

	public Boolean getErweiterteBeduerfnisse() {
		return erweiterteBeduerfnisse;
	}

	public void setErweiterteBeduerfnisse(Boolean erweiterteBeduerfnisse) {
		this.erweiterteBeduerfnisse = erweiterteBeduerfnisse;
	}

	@Nullable
	public LocalDate getDatumAblehnung() {
		return datumAblehnung;
	}

	public void setDatumAblehnung(@Nullable LocalDate datumAblehnung) {
		this.datumAblehnung = datumAblehnung;
	}

	@Nullable
	public LocalDate getDatumBestaetigung() {
		return datumBestaetigung;
	}

	public void setDatumBestaetigung(@Nullable LocalDate datumBestaetigung) {
		this.datumBestaetigung = datumBestaetigung;
	}

	@Override
	public int compareTo(@Nonnull JaxAbstractDTO o) {
		if (o instanceof JaxBetreuung) {
			final JaxBetreuung other = (JaxBetreuung) o;
			return getBetreuungNummer().compareTo(other.getBetreuungNummer());
		}
		return super.compareTo(o);
	}
}
