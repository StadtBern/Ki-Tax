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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ejb.Local;
import javax.ejb.Stateless;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.services.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.services.vorlagen.VerfuegungsmusterImpl;
import ch.dvbern.ebegu.services.vorlagen.VerfuegungsmusterMergeSource;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;

/**
 * Implementiert VerfuegungsGenerierungPDFService
 */
@Stateless
@Local(VerfuegungsGenerierungPDFService.class)
public class VerfuegungsGenerierungPDFServiceBean extends AbstractBaseService implements VerfuegungsGenerierungPDFService {

	@Override
	public List<byte[]> generiereVerfuegungen(Gesuch gesuch) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		List<byte[]> result = new ArrayList<>();

		DOCXMergeEngine docxME = new DOCXMergeEngine("Verfuegungsmuster");

		try {

			for (KindContainer kindContainer : gesuch.getKindContainers()) {
				for (Betreuung betreuung : kindContainer.getBetreuungen()) {
					// Pro Betreuung ein Dokument
					InputStream is = this.getClass().getResourceAsStream("/vorlagen/Verfuegungsmuster.docx");
					Objects.requireNonNull(is, "Verfuegungsmuster.docx nicht gefunden");
					result.add(new GeneratePDFDocumentHelper().generatePDFDocument(docxME.getDocument(is, new VerfuegungsmusterMergeSource(new VerfuegungsmusterImpl(betreuung)))));
					is.close();
				}
			}

		} catch (Exception e) {
			throw new MergeDocException("generiereVerfuegung()", "Bei der Generierung der Verfuegungsmustervorlage ist einen Fehler aufgetretten", e, new Objects[] {});
		}
		return result;
	}
}
