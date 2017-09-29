package ch.dvbern.ebegu.api.dtos;

import java.time.LocalDate;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.date.converters.LocalDateXMLConverter;

/**
 * DTO fuer Familiensituationen
 */
@XmlRootElement(name = "einkommensverschlechterunginfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEinkommensverschlechterungInfo extends JaxAbstractDTO {

	private static final long serialVersionUID = 735198205986579755L;

	@NotNull
	private Boolean einkommensverschlechterung = Boolean.FALSE;

	@NotNull
	private Boolean ekvFuerBasisJahrPlus1;

	@NotNull
	private Boolean ekvFuerBasisJahrPlus2;

	@Nullable
	private Boolean gemeinsameSteuererklaerung_BjP1;

	@Nullable
	private Boolean gemeinsameSteuererklaerung_BjP2;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	private String grundFuerBasisJahrPlus1;

	@Size(max = Constants.DB_DEFAULT_MAX_LENGTH)
	@Nullable
	private String grundFuerBasisJahrPlus2;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate stichtagFuerBasisJahrPlus1;

	@Nullable
	@XmlJavaTypeAdapter(LocalDateXMLConverter.class)
	private LocalDate stichtagFuerBasisJahrPlus2;

	@NotNull
	private Boolean ekvBasisJahrPlus1Annulliert;

	@NotNull
	private Boolean ekvBasisJahrPlus2Annulliert;


	public Boolean getEinkommensverschlechterung() {
		return einkommensverschlechterung;
	}

	public void setEinkommensverschlechterung(final Boolean einkommensverschlechterung) {
		this.einkommensverschlechterung = einkommensverschlechterung;
	}

	public Boolean getEkvFuerBasisJahrPlus1() {
		return ekvFuerBasisJahrPlus1;
	}

	public void setEkvFuerBasisJahrPlus1(final Boolean ekvFuerBasisJahrPlus1) {
		this.ekvFuerBasisJahrPlus1 = ekvFuerBasisJahrPlus1;
	}

	public Boolean getEkvFuerBasisJahrPlus2() {
		return ekvFuerBasisJahrPlus2;
	}

	public void setEkvFuerBasisJahrPlus2(final Boolean ekvFuerBasisJahrPlus2) {
		this.ekvFuerBasisJahrPlus2 = ekvFuerBasisJahrPlus2;
	}

	@Nullable
	public String getGrundFuerBasisJahrPlus1() {
		return grundFuerBasisJahrPlus1;
	}

	public void setGrundFuerBasisJahrPlus1(@Nullable final String grundFuerBasisJahrPlus1) {
		this.grundFuerBasisJahrPlus1 = grundFuerBasisJahrPlus1;
	}

	@Nullable
	public String getGrundFuerBasisJahrPlus2() {
		return grundFuerBasisJahrPlus2;
	}

	public void setGrundFuerBasisJahrPlus2(@Nullable final String grundFuerBasisJahrPlus2) {
		this.grundFuerBasisJahrPlus2 = grundFuerBasisJahrPlus2;
	}

	@Nullable
	public LocalDate getStichtagFuerBasisJahrPlus1() {
		return stichtagFuerBasisJahrPlus1;
	}

	public void setStichtagFuerBasisJahrPlus1(@Nullable final LocalDate stichtagFuerBasisJahrPlus1) {
		this.stichtagFuerBasisJahrPlus1 = stichtagFuerBasisJahrPlus1;
	}

	@Nullable
	public LocalDate getStichtagFuerBasisJahrPlus2() {
		return stichtagFuerBasisJahrPlus2;
	}

	public void setStichtagFuerBasisJahrPlus2(@Nullable final LocalDate stichtagFuerBasisJahrPlus2) {
		this.stichtagFuerBasisJahrPlus2 = stichtagFuerBasisJahrPlus2;
	}

	@Nullable
	public Boolean getGemeinsameSteuererklaerung_BjP1() {
		return gemeinsameSteuererklaerung_BjP1;
	}

	public void setGemeinsameSteuererklaerung_BjP1(@Nullable Boolean gemeinsameSteuererklaerung_BjP1) {
		this.gemeinsameSteuererklaerung_BjP1 = gemeinsameSteuererklaerung_BjP1;
	}

	@Nullable
	public Boolean getGemeinsameSteuererklaerung_BjP2() {
		return gemeinsameSteuererklaerung_BjP2;
	}

	public void setGemeinsameSteuererklaerung_BjP2(@Nullable Boolean gemeinsameSteuererklaerung_BjP2) {
		this.gemeinsameSteuererklaerung_BjP2 = gemeinsameSteuererklaerung_BjP2;
	}

	public Boolean getEkvBasisJahrPlus1Annulliert() {
		return ekvBasisJahrPlus1Annulliert;
	}

	public void setEkvBasisJahrPlus1Annulliert(Boolean ekvBasisJahrPlus1Annulliert) {
		this.ekvBasisJahrPlus1Annulliert = ekvBasisJahrPlus1Annulliert;
	}

	public Boolean getEkvBasisJahrPlus2Annulliert() {
		return ekvBasisJahrPlus2Annulliert;
	}

	public void setEkvBasisJahrPlus2Annulliert(Boolean ekvBasisJahrPlus2Annulliert) {
		this.ekvBasisJahrPlus2Annulliert = ekvBasisJahrPlus2Annulliert;
	}
}
