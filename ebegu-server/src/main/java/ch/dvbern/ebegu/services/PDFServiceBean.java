package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.vorlagen.GeneratePDFDocumentHelper;
import ch.dvbern.ebegu.vorlagen.begleitschreiben.BegleitschreibenPrintImpl;
import ch.dvbern.ebegu.vorlagen.begleitschreiben.BegleitschreibenPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.finanziellesituation.BerechnungsgrundlagenInformationPrintImpl;
import ch.dvbern.ebegu.vorlagen.finanziellesituation.FinanzielleSituationEinkommensverschlechterungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.freigabequittung.FreigabequittungPrintImpl;
import ch.dvbern.ebegu.vorlagen.freigabequittung.FreigabequittungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.mahnung.MahnungPrintImpl;
import ch.dvbern.ebegu.vorlagen.mahnung.MahnungPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.nichteintreten.NichteintretenPrintImpl;
import ch.dvbern.ebegu.vorlagen.nichteintreten.NichteintretenPrintMergeSource;
import ch.dvbern.ebegu.vorlagen.verfuegung.VerfuegungPrintImpl;
import ch.dvbern.ebegu.vorlagen.verfuegung.VerfuegungPrintMergeSource;
import com.google.common.io.ByteStreams;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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

	@Inject
	private Authorizer authorizer;

	@Nonnull
	@Override
	public byte[] generateNichteintreten(Betreuung betreuung, boolean writeProtected) throws MergeDocException {

		EbeguVorlageKey vorlageKey;

		BetreuungsangebotTyp angebotTyp = betreuung.getBetreuungsangebotTyp();

		if (angebotTyp == BetreuungsangebotTyp.KITA
			|| angebotTyp == BetreuungsangebotTyp.TAGESELTERN_KLEINKIND) {
			vorlageKey = EbeguVorlageKey.VORLAGE_NICHT_EINTRETENSVERFUEGUNG;
		} else if (angebotTyp == BetreuungsangebotTyp.TAGI
			|| angebotTyp == BetreuungsangebotTyp.TAGESELTERN_SCHULKIND
			|| angebotTyp == BetreuungsangebotTyp.TAGESSCHULE) {
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
				ByteStreams.toByteArray(is), new NichteintretenPrintMergeSource(new NichteintretenPrintImpl(betreuung)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("generateNichteintreten()",
				"Bei der Generierung der Nichteintreten ist ein Fehler aufgetreten", e, new Objects[]{});
		}

	}

	@Nonnull
	@Override
	public byte[] generateMahnung(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung, boolean writeProtected) throws MergeDocException {

		EbeguVorlageKey vorlageKey;

		switch (mahnung.getMahnungTyp()) {
			case ERSTE_MAHNUNG:
				vorlageKey = EbeguVorlageKey.VORLAGE_MAHNUNG_1;
				break;
			case ZWEITE_MAHNUNG:
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
				ByteStreams.toByteArray(is), new MahnungPrintMergeSource(new MahnungPrintImpl(mahnung, vorgaengerMahnung)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("generateMahnung()",
				"Bei der Generierung der Mahnung ist ein Fehler aufgetreten", e, new Objects[]{});
		}

	}

	@Override
	@Nonnull
	public byte[] generateFreigabequittung(Gesuch gesuch, Zustelladresse zustellAdresse, boolean writeProtected) throws MergeDocException {

		EbeguVorlageKey vorlageKey = EbeguVorlageKey.VORLAGE_FREIGABEQUITTUNG;

		try {
			Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageKey);
			Objects.requireNonNull(is, "Vorlage '" + vorlageKey.name() + "' nicht gefunden");

			final List<DokumentGrund> dokumentGrundsMerged = calculateListOfDokumentGrunds(gesuch);

			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				ByteStreams.toByteArray(is), new FreigabequittungPrintMergeSource(new FreigabequittungPrintImpl(gesuch, zustellAdresse, dokumentGrundsMerged)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("generateFreigabequittung()",
				"Bei der Generierung der Freigabequittung ist ein Fehler aufgetreten", e, new Objects[]{});
		}
	}

	@Override
	@Nonnull
	public byte[] generateBegleitschreiben(@Nonnull Gesuch gesuch, boolean writeProtected) throws MergeDocException {
		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");
		authorizer.checkReadAuthorization(gesuch);

		try {
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(),
				gueltigkeit.getGueltigBis(), EbeguVorlageKey.VORLAGE_BEGLEITSCHREIBEN);
			Objects.requireNonNull(is, "Vorlage fuer Begleitschreiben nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				ByteStreams.toByteArray(is), new BegleitschreibenPrintMergeSource(new BegleitschreibenPrintImpl(gesuch)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("printBegleitschreiben()",
				"Bei der Generierung der Begleitschreibenvorlage ist ein Fehler aufgetreten", e, new Objects[]{});
		}
	}

	@Nonnull
	@Override
	public byte[] generateFinanzielleSituation(@Nonnull Gesuch gesuch, Verfuegung famGroessenVerfuegung, boolean writeProtected) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		authorizer.checkReadAuthorizationFinSit(gesuch);

		try {
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(),
				gueltigkeit.getGueltigBis(), EbeguVorlageKey.VORLAGE_FINANZIELLE_SITUATION);
			Objects.requireNonNull(is, "Vorlage fuer Berechnungsgrundlagen nicht gefunden");
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				ByteStreams.toByteArray(is), new FinanzielleSituationEinkommensverschlechterungPrintMergeSource(new BerechnungsgrundlagenInformationPrintImpl(gesuch, famGroessenVerfuegung)),
				writeProtected);

			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("generateFinanzielleSituation()",
				"Bei der Generierung der Berechnungsgrundlagen ist ein Fehler aufgetreten", e, new Objects[]{});
		}
	}

	@Nonnull
	@Override
	public byte[] generateVerfuegungForBetreuung(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum, boolean writeProtected) throws MergeDocException {

		final DateRange gueltigkeit = betreuung.extractGesuchsperiode().getGueltigkeit();
		EbeguVorlageKey vorlageFromBetreuungsangebottyp = getVorlageFromBetreuungsangebottyp(betreuung);
		InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageFromBetreuungsangebottyp);
		Objects.requireNonNull(is, "Vorlage fuer die Verfuegung nicht gefunden");
		authorizer.checkReadAuthorization(betreuung);
		try {
			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				ByteStreams.toByteArray(is), new VerfuegungPrintMergeSource(new VerfuegungPrintImpl(betreuung, letzteVerfuegungDatum)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("printVerfuegungen()",
				"Bei der Generierung der Verfuegungsmustervorlage ist ein Fehler aufgetreten", e, new Objects[]{});
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
			case TAGESELTERN_KLEINKIND:
				return EbeguVorlageKey.VORLAGE_VERFUEGUNG_TAGESELTERN_KLEINKINDER;
			case TAGESELTERN_SCHULKIND:
				return EbeguVorlageKey.VORLAGE_BRIEF_TAGESELTERN_SCHULKINDER;
			case TAGI:
				return EbeguVorlageKey.VORLAGE_BRIEF_TAGESSTAETTE_SCHULKINDER;
			case KITA:
			default:
				return EbeguVorlageKey.VORLAGE_VERFUEGUNG_KITA;
		}
	}

	/**
	 * In dieser Methode werden alle DokumentGrunds vom Gesuch einer Liste hinzugefuegt. Die die bereits existieren und die
	 * die noch nicht hochgeladen wurden
	 *
	 * @param gesuch
	 * @return
	 */
	private List<DokumentGrund> calculateListOfDokumentGrunds(Gesuch gesuch) {
		List<DokumentGrund> dokumentGrundsMerged = new ArrayList<>();
		dokumentGrundsMerged.addAll(DokumenteUtil
			.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(gesuch),
				dokumentGrundService.findAllDokumentGrundByGesuch(gesuch), gesuch));
		Collections.sort(dokumentGrundsMerged);
		return dokumentGrundsMerged;
	}
}
