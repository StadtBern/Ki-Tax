package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

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
	private JaxAdresse adresse;

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

}
