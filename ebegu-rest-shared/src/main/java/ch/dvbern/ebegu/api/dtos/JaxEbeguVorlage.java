package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import ch.dvbern.ebegu.enums.EbeguVorlageKey;

/**
 * DTO fuer zeitabhängige E-BEGU-Parameter
 */
@XmlRootElement(name = "ebeguVorlage")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxEbeguVorlage extends JaxAbstractDateRangedDTO {

	private static final long serialVersionUID = 2539868697910194410L;

	@NotNull
	private JaxVorlage vorlage;

	@NotNull
	private EbeguVorlageKey name = null;

	private boolean proGesuchsperiode;

	public JaxVorlage getVorlage() {
		return vorlage;
	}

	public void setVorlage(JaxVorlage vorlage) {
		this.vorlage = vorlage;
	}

	public EbeguVorlageKey getName() {
		return name;
	}

	public void setName(EbeguVorlageKey name) {
		this.name = name;
	}

	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}
}
