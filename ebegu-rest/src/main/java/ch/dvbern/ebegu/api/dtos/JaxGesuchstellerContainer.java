package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * DTO fuer Stammdaten der GesuchstellerContainer (kennt adresse)
 */
@XmlRootElement(name = "gesuchsteller")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxGesuchstellerContainer extends JaxAbstractDTO {

	private static final long serialVersionUID = -1217011301387130097L;

	@Valid
	private JaxGesuchsteller gesuchstellerGS;

	@Valid
	private JaxGesuchsteller gesuchstellerJA;

	//Adressen
	@NotNull
	@Valid
	private List<JaxAdresseContainer> adressen;

	@Valid
	private JaxAdresseContainer alternativeAdresse;

	@Valid
	private JaxFinanzielleSituationContainer finanzielleSituationContainer;

	@Valid
	@Nullable
	private JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer;

	private Collection<JaxErwerbspensumContainer> erwerbspensenContainers = new LinkedHashSet<>();


	public JaxGesuchsteller getGesuchstellerGS() {
		return gesuchstellerGS;
	}

	public void setGesuchstellerGS(JaxGesuchsteller gesuchstellerGS) {
		this.gesuchstellerGS = gesuchstellerGS;
	}

	public JaxGesuchsteller getGesuchstellerJA() {
		return gesuchstellerJA;
	}

	public void setGesuchstellerJA(JaxGesuchsteller gesuchstellerJA) {
		this.gesuchstellerJA = gesuchstellerJA;
	}

	public List<JaxAdresseContainer> getAdressen() {
		return adressen;
	}

	public void setAdressen(final List<JaxAdresseContainer> adressen) {
		this.adressen = adressen;
	}

	public JaxAdresseContainer getAlternativeAdresse() {
		return alternativeAdresse;
	}

	public void setAlternativeAdresse(final JaxAdresseContainer alternativeAdresse) {
		this.alternativeAdresse = alternativeAdresse;
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

	@Nullable
	public JaxEinkommensverschlechterungContainer getEinkommensverschlechterungContainer() {
		return einkommensverschlechterungContainer;
	}

	public void setEinkommensverschlechterungContainer(@Nullable JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer) {
		this.einkommensverschlechterungContainer = einkommensverschlechterungContainer;
	}

	public void addAdresse(JaxAdresseContainer adresse) {
		if (adressen == null) {
			adressen = new ArrayList<>();
		}
		adressen.add(adresse);
	}
}
