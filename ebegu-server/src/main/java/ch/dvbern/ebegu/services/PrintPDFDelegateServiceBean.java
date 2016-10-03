package ch.dvbern.ebegu.services;
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

import java.util.List;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.MergeDocException;

/**
 * Implementiert PrintDokumentDelegateService
 */
@Stateless
@Local(PrintDokumentDelegateService.class)
public class PrintPDFDelegateServiceBean extends AbstractBaseService implements PrintDokumentDelegateService {

	@Inject
	private PrintBegleitschreibenPDFService printBegleitschreibenPDFService;

	@Inject
	private PrintVerfuegungPDFService verfuegungsGenerierungPDFService;

	@Inject
	private PrintFinanzielleSituationPDFService printFinanzielleSituationService;

	@Inject
	private PrintFamilienStituationPDFService printFamilienStituationPDFService;

	@Nonnull
	@Override
	public byte[] printBegleitschreiben(@Nonnull Gesuch gesuch) throws MergeDocException {

		return printBegleitschreibenPDFService.printBegleitschreiben(gesuch);
	}

	@Nonnull
	@Override
	public List<byte[]> printVerfuegung(@Nonnull Gesuch gesuch) throws MergeDocException {

		return verfuegungsGenerierungPDFService.printVerfuegungen(gesuch);
	}

	@Nonnull
	@Override
	public byte[] printFinanziellenSituation(@Nonnull Gesuch gesuch) throws MergeDocException {

		return printFinanzielleSituationService.printFinanzielleSituation(gesuch);
	}

	@Nonnull
	@Override
	public byte[] printFamilienSituation(@Nonnull Gesuch gesuch) throws MergeDocException {

		return printFamilienStituationPDFService.printPrintFamilienSituation(gesuch);
	}
}
