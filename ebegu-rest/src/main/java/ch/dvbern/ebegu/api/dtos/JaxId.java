package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;

/**
 * Wrapper fuer ID der DTO Klassen. Dient zum einfachen verwalten der Validierungsannotationen von
 */
public class JaxId {

	@NotNull
	private final String id;

	public JaxId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
