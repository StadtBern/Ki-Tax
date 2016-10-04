package ch.dvbern.ebegu.services;

import java.util.List;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.MergeDocException;

/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 18.08.2016
*/
public interface PrintDokumentDelegateService {

	/**
	 * Bereitet das Begleitschreiben auf
	 *
	 * @param gesuch das Gesuch
	 * @return Liste der generierten Verfuegungen Pro Kind
	 * @throws MergeDocException Falls bei der Verfuegungsgenerierung einen Fehler auftritt
	 */
	@Nonnull
	byte[] printBegleitschreiben(@Nonnull Gesuch gesuch) throws MergeDocException;

	/**
	 * Bereitet die Verfuegungsdokumente auf
	 *
	 * @param gesuch das Gesuch
	 * @return Liste der generierten Verfuegungen Pro Kind
	 * @throws MergeDocException Falls bei der Verfuegungsgenerierung einen Fehler auftritt
	 */
	@Nonnull
	List<byte[]> printVerfuegung(@Nonnull Gesuch gesuch) throws MergeDocException;

	/**
	 * Bereitet die Verfuegungsdokumente auf
	 *
	 * @param gesuch das Gesuch
	 * @return Liste der generierten Verfuegungen Pro Kind
	 * @throws MergeDocException Falls bei der Verfuegungsgenerierung einen Fehler auftritt
	 */
	@Nonnull
	byte[] printFinanziellenSituation(@Nonnull Gesuch gesuch) throws MergeDocException;

	/**
	 * Bereitet die Familiensituation auf
	 *
	 * @param gesuch das Gesuch
	 * @return Das PDF Dokument in Byte Format
	 * @throws MergeDocException Falls bei der Generierung einen Fehler auftritt
	 */
	@Nonnull
	byte[] printFamilienSituation(@Nonnull Gesuch gesuch) throws MergeDocException;

}
