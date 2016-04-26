package ch.dvbern.ebegu.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Wrapper fuer ID der DTO Klassen. Dient zum einfachen verwalten der Validierungsannotationen von
 */
public class JaxId {

	@NotNull
	private String id;

	public JaxId(@JsonProperty("id") String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
