package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO fuer Faelle
 */
@XmlRootElement(name = "gesuch")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuch extends JaxAbstractAntragDTO {

	private static final long serialVersionUID = -1217019901364130097L;

	@Nullable
	private JaxGesuchstellerContainer gesuchsteller1;

	@Nullable
	private JaxGesuchstellerContainer gesuchsteller2;

	@NotNull
	private Set<JaxKindContainer> kindContainers = new LinkedHashSet<>();

	@Nullable
	private JaxFamiliensituationContainer familiensituationContainer;

	@Nullable
	private JaxEinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer;

	@Nullable
	private String bemerkungen;

	@Nullable
	private String bemerkungenSTV;

	@Nullable
	private String bemerkungenPruefungSTV;

	private int laufnummer;

	private boolean geprueftSTV;

	private boolean hasFSDokument;

	private boolean gesperrtWegenBeschwerde;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumGewarntNichtFreigegeben;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumGewarntFehlendeQuittung;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate datumVerfuegt;

	private boolean gueltig;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Nullable
	public JaxGesuchstellerContainer getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable final JaxGesuchstellerContainer gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public JaxGesuchstellerContainer getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable final JaxGesuchstellerContainer gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<JaxKindContainer> getKindContainers() {
		return kindContainers;
	}

	public void setKindContainers(final Set<JaxKindContainer> kindContainers) {
		this.kindContainers = kindContainers;
	}

	@Nullable
	public JaxFamiliensituationContainer getFamiliensituationContainer() {
		return familiensituationContainer;
	}

	public void setFamiliensituationContainer(@Nullable JaxFamiliensituationContainer familiensituationContainer) {
		this.familiensituationContainer = familiensituationContainer;
	}

	@Nullable
	public JaxEinkommensverschlechterungInfoContainer getEinkommensverschlechterungInfoContainer() {
		return einkommensverschlechterungInfoContainer;
	}

	public void setEinkommensverschlechterungInfoContainer(@Nullable final JaxEinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainer) {
		this.einkommensverschlechterungInfoContainer = einkommensverschlechterungInfoContainer;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	@Nullable
	public String getBemerkungenSTV() {
		return bemerkungenSTV;
	}

	public void setBemerkungenSTV(@Nullable String bemerkungenSTV) {
		this.bemerkungenSTV = bemerkungenSTV;
	}

	@Nullable
	public String getBemerkungenPruefungSTV() {
		return bemerkungenPruefungSTV;
	}

	public void setBemerkungenPruefungSTV(@Nullable String bemerkungenPruefungSTV) {
		this.bemerkungenPruefungSTV = bemerkungenPruefungSTV;
	}

	@Nullable
	public int getLaufnummer() {
		return laufnummer;
	}

	public void setLaufnummer(@Nullable int laufnummer) {
		this.laufnummer = laufnummer;
	}

	public boolean isGeprueftSTV() {
		return geprueftSTV;
	}

	public void setGeprueftSTV(boolean geprueftSTV) {
		this.geprueftSTV = geprueftSTV;
	}

	@Nullable
	public boolean isHasFSDokument() {
		return hasFSDokument;
	}

	public void setHasFSDokument(@Nullable boolean hasFSDokument) {
		this.hasFSDokument = hasFSDokument;
	}

	public boolean isGesperrtWegenBeschwerde() {
		return gesperrtWegenBeschwerde;
	}

	public void setGesperrtWegenBeschwerde(boolean gesperrtWegenBeschwerde) {
		this.gesperrtWegenBeschwerde = gesperrtWegenBeschwerde;
	}

	@Nullable
	public LocalDate getDatumGewarntNichtFreigegeben() {
		return datumGewarntNichtFreigegeben;
	}

	public void setDatumGewarntNichtFreigegeben(@Nullable LocalDate datumGewarntNichtFreigegeben) {
		this.datumGewarntNichtFreigegeben = datumGewarntNichtFreigegeben;
	}

	@Nullable
	public LocalDate getDatumGewarntFehlendeQuittung() {
		return datumGewarntFehlendeQuittung;
	}

	public void setDatumGewarntFehlendeQuittung(@Nullable LocalDate datumGewarntFehlendeQuittung) {
		this.datumGewarntFehlendeQuittung = datumGewarntFehlendeQuittung;
	}

	@Nullable
	public LocalDate getDatumVerfuegt() {
		return datumVerfuegt;
	}

	public void setDatumVerfuegt(@Nullable LocalDate datumVerfuegt) {
		this.datumVerfuegt = datumVerfuegt;
	}

	public boolean isGueltig() {
		return gueltig;
	}

	public void setGueltig(boolean gueltig) {
		this.gueltig = gueltig;
	}
}

