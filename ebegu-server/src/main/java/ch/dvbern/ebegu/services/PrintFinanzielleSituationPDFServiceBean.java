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
* Ersteller: zeab am: 19.08.2016
*/

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.vorlagen.BerechnungsgrundlagenInformationPrintImpl;
import ch.dvbern.ebegu.vorlagen.FinanzielleSituationEinkommensverschlechterungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;

/**
 * Implementiert PrintFinanzielleSituationService
 */
@Stateless
@Local(PrintFinanzielleSituationPDFService.class)
public class PrintFinanzielleSituationPDFServiceBean extends AbstractBaseService implements PrintFinanzielleSituationPDFService {

	@Nonnull
	@Override
	public byte[] printFinanzielleSituation(@Nonnull Gesuch gesuch) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		DOCXMergeEngine docxME = new DOCXMergeEngine("FinanzielleSituation");

		try {
			// Pro Betreuung ein Dokument
			InputStream is = this.getClass().getResourceAsStream("/vorlagen/Berechnungsgrundlagen.docx");
			Objects.requireNonNull(is, "Berechnungsgrundlagen.docx nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper()
					.generatePDFDocument(docxME.getDocument(is, new FinanzielleSituationEinkommensverschlechterungPrintMergeSource(new BerechnungsgrundlagenInformationPrintImpl(gesuch))));
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("generiereVerfuegung()", "Bei der Generierung der Berechnungsgrundlagen ist einen Fehler aufgetreten", e, new Objects[] {});
		}
	}
}
