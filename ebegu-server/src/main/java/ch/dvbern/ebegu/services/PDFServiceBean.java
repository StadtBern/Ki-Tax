/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.EbeguUtil;
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

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

@Stateless
@Local(PDFService.class)
public class PDFServiceBean extends AbstractPrintService implements PDFService {

	private static final Objects[] OBJECTARRAY = {};
	public static final byte[] BYTES = new byte[0];

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Inject
	private Authorizer authorizer;

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public byte[] generateNichteintreten(Betreuung betreuung, boolean writeProtected) throws
		MergeDocException {

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
				"Unexpected Betreuung Type", null, OBJECTARRAY);
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
				"Bei der Generierung der Nichteintreten ist ein Fehler aufgetreten", e, OBJECTARRAY);
		}
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public byte[] generateMahnung(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung,
		boolean writeProtected) throws MergeDocException {

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
				"Unexpected Mahnung Type", null, OBJECTARRAY);
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
				"Bei der Generierung der Mahnung ist ein Fehler aufgetreten", e, OBJECTARRAY);
		}
	}

	@Override
	@Nonnull
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public byte[] generateFreigabequittung(Gesuch gesuch, boolean writeProtected) throws MergeDocException {

		EbeguVorlageKey vorlageKey = EbeguVorlageKey.VORLAGE_FREIGABEQUITTUNG;
		try {
			Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");
			final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
			InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis(), vorlageKey);
			Objects.requireNonNull(is, "Vorlage '" + vorlageKey.name() + "' nicht gefunden");

			final List<DokumentGrund> dokumentGrundsMerged = calculateListOfDokumentGrunds(gesuch);

			byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
				ByteStreams.toByteArray(is), new FreigabequittungPrintMergeSource(new FreigabequittungPrintImpl(gesuch, dokumentGrundsMerged)),
				writeProtected);
			is.close();
			return bytes;
		} catch (IOException e) {
			throw new MergeDocException("generateFreigabequittung()",
				"Bei der Generierung der Freigabequittung ist ein Fehler aufgetreten", e, OBJECTARRAY);
		}
	}

	@Override
	@Nonnull
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
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
				"Bei der Generierung der Begleitschreibenvorlage ist ein Fehler aufgetreten", e, OBJECTARRAY);
		}
	}

	@Nullable
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, ADMINISTRATOR_SCHULAMT, SCHULAMT, GESUCHSTELLER })
	public byte[] generateFinanzielleSituation(@Nonnull Gesuch gesuch, Verfuegung famGroessenVerfuegung,
		boolean writeProtected) throws MergeDocException {

		Objects.requireNonNull(gesuch, "Das Argument 'gesuch' darf nicht leer sein");

		if (EbeguUtil.isFinanzielleSituationRequired(gesuch)) {

			if (!gesuch.hasOnlyBetreuungenOfSchulamt()) {
				// Bei nur Schulamt prüfen wir die Berechtigung nicht, damit das JA solche Gesuche schliessen kann. Der UseCase ist, dass zuerst ein zweites
				// Angebot vorhanden war, dieses aber durch das JA gelöscht wurde.		authorizer.checkReadAuthorizationFinSit(gesuch);
				authorizer.checkReadAuthorizationFinSit(gesuch);
			}
			try {
				final DateRange gueltigkeit = gesuch.getGesuchsperiode().getGueltigkeit();
				InputStream is = getVorlageStream(gueltigkeit.getGueltigAb(),
					gueltigkeit.getGueltigBis(), EbeguVorlageKey.VORLAGE_FINANZIELLE_SITUATION);
				Objects.requireNonNull(is, "Vorlage fuer Berechnungsgrundlagen nicht gefunden");
				byte[] bytes = new GeneratePDFDocumentHelper().generatePDFDocument(
					ByteStreams.toByteArray(is), new FinanzielleSituationEinkommensverschlechterungPrintMergeSource(
						new BerechnungsgrundlagenInformationPrintImpl(gesuch, famGroessenVerfuegung)), writeProtected);

				is.close();
				return bytes;
			} catch (IOException e) {
				throw new MergeDocException("generateFinanzielleSituation()",
					"Bei der Generierung der Berechnungsgrundlagen ist ein Fehler aufgetreten", e, OBJECTARRAY);
			}
		}
		return BYTES;
	}

	@Nonnull
	@Override
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public byte[] generateVerfuegungForBetreuung(Betreuung betreuung,
		@Nullable LocalDate letzteVerfuegungDatum, boolean writeProtected) throws MergeDocException {

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
				"Bei der Generierung der Verfuegungsmustervorlage ist ein Fehler aufgetreten", e, OBJECTARRAY);
		}
	}

	@Nonnull
	private EbeguVorlageKey getVorlageFromBetreuungsangebottyp(final Betreuung betreuung) {
		if (Betreuungsstatus.NICHT_EINGETRETEN == betreuung.getBetreuungsstatus()) {
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
	 */
	private List<DokumentGrund> calculateListOfDokumentGrunds(Gesuch gesuch) {
		List<DokumentGrund> dokumentGrundsMerged = new ArrayList<>(DokumenteUtil
			.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(gesuch),
				dokumentGrundService.findAllDokumentGrundByGesuch(gesuch), gesuch));
		Collections.sort(dokumentGrundsMerged);
		return dokumentGrundsMerged;
	}
}
