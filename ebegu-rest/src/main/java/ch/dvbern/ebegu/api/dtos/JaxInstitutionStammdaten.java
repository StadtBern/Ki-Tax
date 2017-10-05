package ch.dvbern.ebegu.api.dtos;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer InstitutionStammdaten
 */
@XmlRootElement(name = "institutionStammdaten")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxInstitutionStammdaten extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -1893677808323618626L;
	@Nullable
	private String iban;
	@Nullable
	private BigDecimal oeffnungstage;
	@Nullable
	private BigDecimal oeffnungsstunden;
	@Nullable
	private BetreuungsangebotTyp betreuungsangebotTyp;
	@NotNull
	private JaxInstitution institution;

	@NotNull
	private Set<JaxModul> module = new LinkedHashSet<>();


	@NotNull
	private JaxAdresse adresse;

	@Nullable
	private String kontoinhaber;
	@Nullable
	private JaxAdresse adresseKontoinhaber;


	@Nullable
	public String getIban() {
		return iban;
	}

	public void setIban(@Nullable String iban) {
		this.iban = iban;
	}

	@Nullable
	public BigDecimal getOeffnungstage() {
		return oeffnungstage;
	}

	public void setOeffnungstage(@Nullable BigDecimal oeffnungstage) {
		this.oeffnungstage = oeffnungstage;
	}

	@Nullable
	public BigDecimal getOeffnungsstunden() {
		return oeffnungsstunden;
	}

	public void setOeffnungsstunden(@Nullable BigDecimal oeffnungsstunden) {
		this.oeffnungsstunden = oeffnungsstunden;
	}

	@Nullable
	public BetreuungsangebotTyp getBetreuungsangebotTyp() {
		return betreuungsangebotTyp;
	}

	public void setBetreuungsangebotTyp(@Nullable BetreuungsangebotTyp betreuungsangebotTyp) {
		this.betreuungsangebotTyp = betreuungsangebotTyp;
	}

	public JaxInstitution getInstitution() {
		return institution;
	}

	public void setInstitution(JaxInstitution institution) {
		this.institution = institution;
	}

	public JaxAdresse getAdresse() {
		return adresse;
	}

	public void setAdresse(JaxAdresse adresse) {
		this.adresse = adresse;
	}

	@Nullable
	public String getKontoinhaber() {
		return kontoinhaber;
	}

	public void setKontoinhaber(@Nullable String kontoinhaber) {
		this.kontoinhaber = kontoinhaber;
	}

	@Nullable
	public JaxAdresse getAdresseKontoinhaber() {
		return adresseKontoinhaber;
	}

	public void setAdresseKontoinhaber(@Nullable JaxAdresse adresseKontoinhaber) {
		this.adresseKontoinhaber = adresseKontoinhaber;
	}

	public Set<JaxModul> getModule() {
		return module;
	}

	public void setModule(Set<JaxModul> module) {
		this.module = module;
	}
}
