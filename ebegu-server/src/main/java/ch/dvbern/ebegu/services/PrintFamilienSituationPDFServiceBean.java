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
* Ersteller: zeab am: 09.08.2016
*/

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;

import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.vorlagen.berechnungsblatt.FamilienSituaionPrintImpl;
import ch.dvbern.ebegu.vorlagen.berechnungsblatt.FamilienSituaionPrintMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;

/**
 * Implementiert VerfuegungsGenerierungPDFService
 */
@Stateless
@Local(PrintBegleitschreibenPDFService.class)
public class PrintFamilienSituationPDFServiceBean extends AbstractPrintService implements PrintFamilienStituationPDFService {

	@Nonnull
	@Override
	public byte[] printPrintFamilienSituation(@Nonnull Gesuch gesuch) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		DOCXMergeEngine docxME = new DOCXMergeEngine("Familiensituation");

		try {
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), EbeguVorlageKey.VORLAGE_FAMILIENSITUATION,
					"/vorlagen/Familiensituation.docx");
			Objects.requireNonNull(is, "Vorlage fuer Familiensituation nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(docxME.getDocument(is, new FamilienSituaionPrintMergeSource(new FamilienSituaionPrintImpl(gesuch))));
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("printPrintFamilienStituation()", "Bei der Generierung der Familiensituationvorlage ist ein Fehler aufgetreten", e, new Objects[] {});
		}
	}
}
