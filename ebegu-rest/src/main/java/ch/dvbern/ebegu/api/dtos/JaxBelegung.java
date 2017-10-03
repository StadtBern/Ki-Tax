package ch.dvbern.ebegu.api.dtos;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO fuer Daten der Belegungen.
 */
@XmlRootElement(name = "belegung")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxBelegung extends JaxAbstractDTO {

	private static final long serialVersionUID = -1297972380574937397L;

	@NotNull
	private Set<JaxModul> module = new LinkedHashSet<>();

	public Set<JaxModul> getModule() {
		return module;
	}

	public void setModule(Set<JaxModul> module) {
		this.module = module;
	}
}
