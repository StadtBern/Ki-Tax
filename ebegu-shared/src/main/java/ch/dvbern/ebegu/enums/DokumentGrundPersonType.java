package ch.dvbern.ebegu.enums;

/**
 * Enum for DokumentGrundPersonType
 * 		FREETEXT   --> makes it possible to give a freetext to the person linked with the dokumentgrund (legacy)
 * 		KIND       --> The DokumentGrund is linked with a Kind
 * 		GESUCHSTELLER  --> The DokumentGrund is linked with a Gesuchsteller
 */
public enum DokumentGrundPersonType {
	FREETEXT,
	KIND,
	GESUCHSTELLER
}
