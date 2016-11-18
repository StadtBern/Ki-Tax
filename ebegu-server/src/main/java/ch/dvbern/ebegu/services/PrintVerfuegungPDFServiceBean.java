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

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.vorlagen.verfuegung.VerfuegungPrintImpl;
import ch.dvbern.ebegu.vorlagen.verfuegung.VerfuegungPrintMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementiert VerfuegungsGenerierungPDFService
 */
@Stateless
@Local(PrintVerfuegungPDFService.class)
public class PrintVerfuegungPDFServiceBean extends AbstractPrintService implements PrintVerfuegungPDFService {

	@Inject
	VerfuegungService verfuegungService;

	@Nonnull
	@Override
	@SuppressFBWarnings(value = "UI_INHERITANCE_UNSAFE_GETRESOURCE")
	public List<byte[]> printVerfuegungen(@Nonnull Gesuch gesuch) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		List<byte[]> result = new ArrayList<>();
        for (KindContainer kindContainer : gesuch.getKindContainers()) {
            for (Betreuung betreuung : kindContainer.getBetreuungen()) {
                // Pro Betreuung ein Dokument
				Optional<LocalDate> optVorherigeVerfuegungDate = verfuegungService.findVorgaengerVerfuegungDate(betreuung);
				LocalDate letztesVerfDatum = optVorherigeVerfuegungDate.orElse(null);
				result.add(printVerfuegungForBetreuung(betreuung, letztesVerfDatum));
            }
        }
		return result;
	}

	@Nonnull
	@Override
	public byte[] printVerfuegungForBetreuung(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum) throws MergeDocException {
		final DOCXMergeEngine docxME = new DOCXMergeEngine("Verfuegungsmuster");

		final DateRange gueltigkeit = betreuung.extractGesuchsperiode().getGueltigkeit();
		EbeguVorlageKey vorlageFromBetreuungsangebottyp = getVorlageFromBetreuungsangebottyp(betreuung);
		String defaultVorlagePathFromBetreuungsangebottyp = getDefaultVorlagePathFromBetreuungsangebottyp(betreuung);
		InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(),
			gueltigkeit.getGueltigBis(), vorlageFromBetreuungsangebottyp, defaultVorlagePathFromBetreuungsangebottyp);
		Objects.requireNonNull(is, "Vorlage fuer die Verfuegung nicht gefunden");

		try {
			VerfuegungPrintMergeSource mergeSource = new VerfuegungPrintMergeSource(new VerfuegungPrintImpl(betreuung, letzteVerfuegungDatum));
			byte[] document = docxME.getDocument(is, mergeSource);
			final byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(document);
			is.close();
			return bytes;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("printVerfuegungen()",
				"Bei der Generierung der Verfuegungsmustervorlage ist ein Fehler aufgetreten", e, new Objects[] {});
		}
	}

	/**
	 * Sucht die Vorlage by default je nach dem welchen Angebottype, die Betreuung hat.
	 * Die Vorlage fuer KITA wird im Fehlerfall zurueckgegeben
	 * @param betreuung
	 * @return
	 */
	@Nonnull
	private String getDefaultVorlagePathFromBetreuungsangebottyp(final Betreuung betreuung) {
		if (Betreuungsstatus.NICHT_EINGETRETEN.equals(betreuung.getBetreuungsstatus())) {
			if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
				//key verwenden wie unten
				return "/vorlagen/Nichteintretensverfuegung.docx";
			} else {
				return "/vorlagen/Infoschreiben Maxtarif.docx";
			}
		}
		switch (betreuung.getBetreuungsangebotTyp()) {
			case TAGESELTERN_KLEINKIND: return "/vorlagen/Verfuegungsmuster_tageseltern_kleinkinder.docx";
			case TAGESELTERN_SCHULKIND: return "/vorlagen/Verfuegungsmuster_tageseltern_schulkinder.docx";
			case TAGI: return "/vorlagen/Verfuegungsmuster_tagesstaette_schulkinder.docx";
			case KITA:
			default: return "/vorlagen/Verfuegungsmuster_kita.docx";
		}
	}

	@Nonnull
	private EbeguVorlageKey getVorlageFromBetreuungsangebottyp(final Betreuung betreuung) {
		if (Betreuungsstatus.NICHT_EINGETRETEN.equals(betreuung.getBetreuungsstatus())) {
			if (betreuung.getBetreuungsangebotTyp().isAngebotJugendamtKleinkind()) {
				return EbeguVorlageKey.VORLAGE_NICHT_EINTRETENSVERFUEGUNG;
			} else {
				return EbeguVorlageKey.VORLAGE_INFOSCHREIBEN_MAXIMALTARIF;
			}
		}
		switch (betreuung.getBetreuungsangebotTyp()) {
			case TAGESELTERN_KLEINKIND: return EbeguVorlageKey.VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER;
			case TAGESELTERN_SCHULKIND: return EbeguVorlageKey.VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER;
			case TAGI: return EbeguVorlageKey.VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER;
			case KITA:
			default: return EbeguVorlageKey.VORLAGE_VERFUEGUNG_KITA;
		}
	}
}
