package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.vorlagen.freigabequittung.FreigabequittungPrintImpl;
import ch.dvbern.ebegu.vorlagen.freigabequittung.FreigabequittungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.mahnung.MahnungPrintImpl;
import ch.dvbern.ebegu.vorlagen.mahnung.MahnungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.nichteintreten.NichteintretenPrintImpl;
import ch.dvbern.ebegu.vorlagen.nichteintreten.NichteintretenPrintMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 28/11/2016.
 */
@Stateless
@Local(PDFService.class)
public class PDFServiceBean extends AbstractPrintService implements PDFService {

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;


	@Nonnull
	@Override
	public byte[] generateNichteintreten(Betreuung betreuung) throws MergeDocException {

		DOCXMergeEngine docxME;
		EbeguVorlageKey vorlageKey;

		BetreuungsangebotTyp angebotTyp = betreuung.getBetreuungsangebotTyp();

		if (angebotTyp == BetreuungsangebotTyp.KITA
			|| angebotTyp == BetreuungsangebotTyp.TAGESELTERN_KLEINKIND) {
			docxME = new DOCXMergeEngine("Nichteintretensverfügung");
			vorlageKey = EbeguVorlageKey.VORLAGE_NICHT_EINTRETENSVERFUEGUNG;
		} else if (angebotTyp == BetreuungsangebotTyp.TAGI
			|| angebotTyp == BetreuungsangebotTyp.TAGESELTERN_SCHULKIND
			|| angebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
			docxME = new DOCXMergeEngine("InfoschreibenMaximaltarif");
			vorlageKey = EbeguVorlageKey.VORLAGE_INFOSCHREIBEN_MAXIMALTARIF;
		} else {
			throw new MergeDocException("generateNichteintreten()",
				"Unexpected Betreuung Type", null, new Objects[]{});
		}

		try {
			Objects.requireNonNull(betreuung, "Das Argument 'betreuung' darf nicht leer sein");
			final DateRange gueltigkeit = betreuung.extractGesuch().getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageKey);
			Objects.requireNonNull(is, "Vorlage '" + vorlageKey.name() + "' nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				docxME.getDocument(is, new NichteintretenPrintMergeSource(new NichteintretenPrintImpl(betreuung))));
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("generateNichteintreten()",
				"Bei der Generierung der Nichteintreten ist ein Fehler aufgetreten", e, new Objects[]{});
		}

	}

	@Nonnull
	@Override
	public byte[] generateMahnung(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung) throws MergeDocException {

		DOCXMergeEngine docxME;
		EbeguVorlageKey vorlageKey;

		switch (mahnung.getMahnungTyp()) {
			case ERSTE_MAHNUNG:
				docxME = new DOCXMergeEngine("ErsteMahnung");
				vorlageKey = EbeguVorlageKey.VORLAGE_MAHNUNG_1;
				break;
			case ZWEITE_MAHNUNG:
				docxME = new DOCXMergeEngine("ZweiteMahnung");
				vorlageKey = EbeguVorlageKey.VORLAGE_MAHNUNG_2;
				break;
			default:
				throw new MergeDocException("generateMahnung()",
					"Unexpected Mahnung Type", null, new Objects[]{});
		}

		try {
			Objects.requireNonNull(mahnung, "Das Argument 'mahnung' darf nicht leer sein");
			final DateRange gueltigkeit = mahnung.getGesuch().getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageKey);
			Objects.requireNonNull(is, "Vorlage '" + vorlageKey.name() + "' nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				docxME.getDocument(is, new MahnungPrintMergeSource(new MahnungPrintImpl(mahnung, vorgaengerMahnung))));
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("generateMahnung()",
				"Bei der Generierung der Mahnung ist ein Fehler aufgetreten", e, new Objects[]{});
		}

	}

	@Override
	@Nonnull
	public byte[] generateFreigabequittung(Gesuch gesuch, Zustelladresse zustellAdresse) throws MergeDocException {

		EbeguVorlageKey vorlageKey = EbeguVorlageKey.VORLAGE_FREIGABEQUITTUNG;
		DOCXMergeEngine docxME = new DOCXMergeEngine("Freigabequittung");

		try {
			Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageKey);
			Objects.requireNonNull(is, "Vorlage '" + vorlageKey.name() + "' nicht gefunden");

			final List<DokumentGrund> dokumentGrundsMerged = calculateListOfDokumentGrunds(gesuch);

			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				docxME.getDocument(is, new FreigabequittungPrintMergeSource(new FreigabequittungPrintImpl(gesuch, zustellAdresse, dokumentGrundsMerged))));
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("generateFreigabequittung()",
				"Bei der Generierung der Freigabequittung ist ein Fehler aufgetreten", e, new Objects[]{});
		}
	}

	/**
	 * In dieser Methode werden alle DokumentGrunds vom Gesuch einer Liste hinzugefuegt. Die die bereits existieren und die
	 * die noch nicht hochgeladen wurden
	 * @param gesuch
	 * @return
	 */
	private List<DokumentGrund> calculateListOfDokumentGrunds(Gesuch gesuch) {
		List<DokumentGrund> dokumentGrundsMerged = new ArrayList<>();
		dokumentGrundsMerged.addAll(DokumenteUtil
            .mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(gesuch),
                dokumentGrundService.findAllDokumentGrundByGesuch(gesuch)));
		Collections.sort(dokumentGrundsMerged);
		return dokumentGrundsMerged;
	}
}
