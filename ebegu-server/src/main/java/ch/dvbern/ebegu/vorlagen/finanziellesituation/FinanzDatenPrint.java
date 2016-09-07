package ch.dvbern.ebegu.vorlagen.finanziellesituation;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 22.08.2016
*/

import java.math.BigDecimal;

/**
 * Definiert die Finanzdaten des Berechnungsformulars
 */
public interface FinanzDatenPrint {

	// ****************************************************************************************************************
	// Achtung, die Methodennamen in dieser Klassen duerfen nicht veraendert werden. Sie muessen identisch sein
	// mit den Platzhaltern im Word-Template!
	// ****************************************************************************************************************

	/**
	 * @return Nettolohn gemäss Lohnausweis / Steuererklärung Gesuchsteller1
	 */
	BigDecimal getNettolohnG1();

	/**
	 * @return Nettolohn gemäss Lohnausweis / Steuererklärung Gesuchsteller2
	 */
	BigDecimal getNettolohnG2();

	/**
	 * @return Erhaltene Familienzulagen (sofern nicht bereits im Nettolohn vorhanden
	 */
	BigDecimal getFamilienzulagenG1();

	/**
	 * @return Erhaltene Familienzulagen (sofern nicht bereits im Nettolohn vorhanden
	 */
	BigDecimal getFamilienzulagenG2();

	/**
	 * @return Steuerpflichtiges Ersatzeinkommen (Leistungen aus AHV, IV, ALV, KV, UV, EO usw.)
	 */
	BigDecimal getErsatzeinkommenG1();

	/**
	 * @return Steuerpflichtiges Ersatzeinkommen (Leistungen aus AHV, IV, ALV, KV, UV, EO usw.)
	 */
	BigDecimal getErsatzeinkommenG2();

	/**
	 * @return Erhaltene Unterhaltsbeiträge (Alimente)
	 */
	BigDecimal getUnterhaltsbeitraegeG1();

	/**
	 * @return Erhaltene Unterhaltsbeiträge (Alimente)
	 */
	BigDecimal getUnterhaltsbeitraegeG2();

	/**
	 * @return In der Steuererklärung ausgewiesener Geschäftsgewinn (Durchschnitt der vergangenen drei Jahre)
	 */
	BigDecimal getGeschaeftsgewinnG1();

	/**
	 * @return In der Steuererklärung ausgewiesener Geschäftsgewinn (Durchschnitt der vergangenen drei Jahre)
	 */
	BigDecimal getGeschaeftsgewinnG2();

	/**
	 * Zwischentotal zusammengesetzt aus Nettolohn, erhaltenen Familienzulagen, Ersatzeinkommen, Unterhaltsbeitreagen, Geschaeftsgewinn
	 * @return Zwischentotal Einkünfte beider Gesuchsteller 1
	 */
	BigDecimal getZwischentotalEinkuenfteG1();

	/**
	 * @return Zwischentotal Einkünfte beider Gesuchsteller 2
	 */
	BigDecimal getZwischentotalEinkuenfteG2();

	/**
	 * @return Total Einkünfte
	 */
	BigDecimal getTotalEinkuenfte();

	/**
	 * @return Bruttovermögen Gesuchsteller 1
	 */
	BigDecimal getBruttovermoegenG1();

	/**
	 * @return Bruttovermögen Gesuchsteller 2
	 */
	BigDecimal getBruttovermoegenG2();

	/**
	 * @return Schulden von G1
	 */
	BigDecimal getSchuldenG1();

	/**
	 * @return Schulden von G2
	 */
	BigDecimal getSchuldenG2();

	/**
	 * @return Zwischentotal Nettovermögen beider Gesuchsteller 1
	 */
	BigDecimal getZwischentotalNettovermoegenBeiderGesuchsteller1();

	/**
	 * @return Zwischentotal Nettovermögen beider Gesuchsteller 2
	 */
	BigDecimal getZwischentotalNettovermoegenBeiderGesuchsteller2();

	/**
	 * @return Zwischentotal Nettovermögen insgesamt
	 */
	BigDecimal getZwischentotalNettovermoegenInsgesamt();

	/**
	 * @return 5% Nettovermögen
	 */
	BigDecimal getNettovermoegen();

	/**
	 * @deprecated
	 * @return Abzüge bei einer Familiengrösse von 5 Personen
	 */
	@Deprecated
	BigDecimal getAbzuegeBeiEinerFamiliengroesseVon5Personen();

	/**
	 * @return Total Abzüge
	 */
	BigDecimal getTotalAbzuege();

	/**
	 * @return Total Einkünfte
	 */
	BigDecimal getZusammenzugTotaleinkuenfte();

	/**
	 * @return 5% Nettovermögen
	 */
	BigDecimal getZusammenzugNettovermoegen();

	/**
	 * @return Total Abzüge
	 */
	BigDecimal getZusammenzugTotalAbzuege();

	/**
	 * @return Massgebendes Einkommen (nach Abzug für Familiengrösse
	 */
	BigDecimal getMassgebendesEinkommen();

	/**
	 * @return
	 */
	int getAnzahlPersonen();
}
