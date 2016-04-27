package ch.dvbern.ebegu.api.dtos;

import ch.dvbern.ebegu.converters.LocalDateXMLConverter;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer InstitutionStammdaten
 */
@XmlRootElement(name = "InstitutionStammdaten")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxInstitutionStammdaten extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = -1893677808323618626L;

	@NotNull
	private String iban;
	@NotNull
	private BigDecimal oeffnungstage;
	@NotNull
	private BigDecimal oeffnungsstunden;
	@Nullable
	private BetreuungsangebotTyp betreuungsangebotTyp;
	@NotNull
	private JaxInstitution institution;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public BigDecimal getOeffnungstage() {
		return oeffnungstage;
	}

	public void setOeffnungstage(BigDecimal oeffnungstage) {
		this.oeffnungstage = oeffnungstage;
	}

	public BigDecimal getOeffnungsstunden() {
		return oeffnungsstunden;
	}

	public void setOeffnungsstunden(BigDecimal oeffnungsstunden) {
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
}
