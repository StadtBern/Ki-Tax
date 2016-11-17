package ch.dvbern.ebegu.api.dtos;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
	private JaxGesuchsteller gesuchsteller1;

	@Nullable
	private JaxGesuchsteller gesuchsteller2;

	@NotNull
	private Set<JaxKindContainer> kindContainers = new LinkedHashSet<>();

	@Nullable
	private JaxFamiliensituation familiensituation;

	@Nullable
	private JaxEinkommensverschlechterungInfo einkommensverschlechterungInfo;

	@Nullable
	private String bemerkungen;

	@Nullable
	private int laufnummer;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Nullable
	public JaxGesuchsteller getGesuchsteller1() {
		return gesuchsteller1;
	}

	public void setGesuchsteller1(@Nullable final JaxGesuchsteller gesuchsteller1) {
		this.gesuchsteller1 = gesuchsteller1;
	}

	@Nullable
	public JaxGesuchsteller getGesuchsteller2() {
		return gesuchsteller2;
	}

	public void setGesuchsteller2(@Nullable final JaxGesuchsteller gesuchsteller2) {
		this.gesuchsteller2 = gesuchsteller2;
	}

	public Set<JaxKindContainer> getKindContainers() {
		return kindContainers;
	}

	public void setKindContainers(final Set<JaxKindContainer> kindContainers) {
		this.kindContainers = kindContainers;
	}

	@Nullable
	public JaxFamiliensituation getFamiliensituation() {
		return familiensituation;
	}

	public void setFamiliensituation(@Nullable final JaxFamiliensituation familiensituation) {
		this.familiensituation = familiensituation;
	}

	@Nullable
	public JaxEinkommensverschlechterungInfo getEinkommensverschlechterungInfo() {
		return einkommensverschlechterungInfo;
	}

	public void setEinkommensverschlechterungInfo(@Nullable final JaxEinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		this.einkommensverschlechterungInfo = einkommensverschlechterungInfo;
	}

	@Nullable
	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(@Nullable String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}
	
	@Nullable
	public int getLaufnummer() {
		return laufnummer;
	}

	public void setLaufnummer(@Nullable int laufnummer) {
		this.laufnummer = laufnummer;
	}
}

