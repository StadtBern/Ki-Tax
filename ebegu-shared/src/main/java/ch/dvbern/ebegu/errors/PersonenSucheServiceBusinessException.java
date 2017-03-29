/*
 * Copyright (c) 2012 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.errors;


/**
 * Exception für Fehler,welche vom EWK-Service geliefert werden
 */
public class PersonenSucheServiceBusinessException extends Exception {

	private static final long serialVersionUID = 5438097529958118878L;

	private String code;
	private String text;

	public PersonenSucheServiceBusinessException(final String code, final String text) {
		super("Code=" + code + ", Text=" + text);
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
