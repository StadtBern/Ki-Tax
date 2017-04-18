package ch.dvbern.ebegu.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuung_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.KindContainer_;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt_;
import ch.dvbern.ebegu.entities.Verfuegung_;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.entities.Zahlungsauftrag_;
import ch.dvbern.ebegu.entities.Zahlungsposition;
import ch.dvbern.ebegu.entities.Zahlungsposition_;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.VerfuegungsZeitabschnittZahlungsstatus;
import ch.dvbern.ebegu.enums.ZahlungStatus;
import ch.dvbern.ebegu.enums.ZahlungauftragStatus;
import ch.dvbern.ebegu.enums.ZahlungspositionStatus;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;


/**
 * Service fuer Zahlungen. Die Zahlungen werden folgendermassen generiert:
 * Wenn ein neuer Zahlungsauftrag erstellt wird, muss nur ein Faelligkeitsdatum mitgegeben werden. Dieses wird *nur* fuer
 * das Abfuellen des XML Files (ISO-20022) verwendet. Fuer die Ermittlung der einzuschliessenden Zahlungsdetail wird
 * immer der aktuelle Timestamp verwendet. Dies, damit wir fuer den naechsten Zahlungsauftrag immer wissen, welche
 * Zahlungen bereits beruecksichtigt wurden.
 * Wir muessen mit 2 Zeitraeumen arbeiten:
 * |      Jan      |      Feb       |
 * |                  |
 * letzter            aktueller
 * Zahlungslauf		Zahlungslauf
 * Für die Ermittlung der "normalen" Zahlungen wird immer (mind.) ein ganzer Monat berücksichtigt, und zwar der aktuelle
 * Monat des Zahlungslaufes plus fruehere Monate, falls in diesen kein Zahlungslauf stattfand.
 * Für die Ermittlung der Korrektur-Zahlungen muessen alle Verfuegungen berücksichtigt werden, welche seit dem letzten
 * Zahlungslauf bis heute dazugekommen sind.
 */
@Stateless
@Local(ZahlungService.class)
@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals", "SpringAutowiredFieldsWarningInspection", "InstanceMethodNamingConvention"})
public class ZahlungServiceBean extends AbstractBaseService implements ZahlungService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZahlungServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Zahlungsauftrag> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragErstellen(@Nonnull LocalDate datumFaelligkeit, @Nonnull String beschreibung) {
		return zahlungsauftragErstellen(datumFaelligkeit, beschreibung, LocalDateTime.now());
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragErstellen(@Nonnull LocalDate datumFaelligkeit, @Nonnull String beschreibung, @Nonnull LocalDateTime datumGeneriert) {
		// Es darf immer nur ein Zahlungsauftrag im Status ENTWURF sein
		Optional<Zahlungsauftrag> lastZahlungsauftrag = findLastZahlungsauftrag();
		if (lastZahlungsauftrag.isPresent() && lastZahlungsauftrag.get().getStatus().isEntwurf()) {
			throw new EbeguRuntimeException("zahlungsauftragErstellen", ErrorCodeEnum.ERROR_ZAHLUNG_ERSTELLEN);
		}

		LOGGER.info("Erstelle Zahlungsauftrag mit Faelligkeit: " + Constants.DATE_FORMATTER.format(datumFaelligkeit));
		Zahlungsauftrag zahlungsauftrag = new Zahlungsauftrag();
		zahlungsauftrag.setStatus(ZahlungauftragStatus.ENTWURF);
		zahlungsauftrag.setBeschrieb(beschreibung);
		zahlungsauftrag.setDatumFaellig(datumFaelligkeit);
		zahlungsauftrag.setDatumGeneriert(datumGeneriert);

		// Alle aktuellen (d.h. der letzte Antrag jedes Falles) Verfuegungen suchen, welche ein Kita-Angebot haben
		// Wir brauchen folgende Daten:
		// - Zeitraum, welcher fuer die (normale) Auszahlung gilt: Immer ganzer Monat, mindestens der Monat des DatumFaellig,
		// 		jedoch seit Ende Monat des letzten Auftrags -> 1 oder mehrere ganze Monate
		// - Zeitraum, welcher fuer die Berechnung der rueckwirkenden Korrekturen gilt: Zeitpunkt der letzten Zahlungserstellung bis aktueller Zeitpunkt
		// 		(Achtung: Es ist *nicht* das Faelligkeitsdatum relevant, sondern das Erstellungsdatum des letzten Auftrags!)
		// Den letzten Zahlungsauftrag lesen
		LocalDateTime lastZahlungErstellt = Constants.START_OF_DATETIME; // Default, falls dies der erste Auftrag ist
		// Falls es eine Wiederholung des Auftrags ist, muessen nur noch die Korrekturen beruecksichtigt werden, welche
		// seit dem letzten Auftrag erstellt wurden
		boolean isRepetition = false;
		// Falls fuer denselben Zeitraum (oder den letzten Teil davon) schon ein Auftrag vorhanden ist, kann das DatumVon nicht
		// einfach an den letzten Auftrag anschliessen
		LocalDate zeitabschnittVon;
		LocalDate zeitabschnittBis = zahlungsauftrag.getDatumGeneriert().toLocalDate().with(TemporalAdjusters.lastDayOfMonth());

		if (lastZahlungsauftrag.isPresent()) {
			lastZahlungErstellt = lastZahlungsauftrag.get().getDatumGeneriert();
			if (zahlungsauftrag.getDatumGeneriert().toLocalDate().isAfter(lastZahlungsauftrag.get().getGueltigkeit().getGueltigBis())) {
				// Wir beginnen am Anfang des Folgemonats des letzten Auftrags
				zeitabschnittVon = lastZahlungErstellt.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
			} else {
				// Repetition, dh.der Monat ist schon ausgeloest. Wir nehmen den Anfang des aktuellen Monats
				zeitabschnittVon = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
				isRepetition = true;
			}
		} else {
			zeitabschnittVon = lastZahlungErstellt.toLocalDate();
		}

		zahlungsauftrag.setGueltigkeit(new DateRange(zeitabschnittVon, zeitabschnittBis));

		Map<String, Zahlung> zahlungProInstitution = new HashMap<>();

		// "Normale" Zahlungen
		if (!isRepetition) {
			LOGGER.info("Ermittle normale Zahlungen im Zeitraum " + zahlungsauftrag.getGueltigkeit().toRangeString());
			Collection<VerfuegungZeitabschnitt> gueltigeVerfuegungZeitabschnitte = getGueltigeVerfuegungZeitabschnitte(zeitabschnittVon, zeitabschnittBis);
			for (VerfuegungZeitabschnitt zeitabschnitt : gueltigeVerfuegungZeitabschnitte) {
				if (zeitabschnitt.getZahlungsstatus().isNeu()) {
					createZahlungsposition(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
				}
			}
		}
		// Korrekturen
		// Stichtag: Falls es eine Wiederholung des Auftrags ist, wurde der aktuelle Monat bereits ausbezahlt.
		LocalDate stichtagKorrekturen = isRepetition ? zeitabschnittBis : zeitabschnittBis.minusMonths(1);
		// Die Korrekturzahlungen werden seit dem letzten Zahlungsauftrag beruecksichtigt. Falls wir im TEST-Mode sind
		// und ein fiktives "DatumGeneriert" gewaehlt haben, nehmen wir als Datum des letzten Auftrags das timestampErstellt
		// und nicht das (eventuell ebenfalls fiktive) datumGeneriert.
		boolean isTestMode = datumGeneriert.isAfter(LocalDateTime.now());
		if (isTestMode) {
			lastZahlungErstellt = Constants.START_OF_DATETIME;
		}
		Collection<VerfuegungZeitabschnitt> verfuegungsZeitabschnitte = getVerfuegungsZeitabschnitteNachVerfuegungDatum(lastZahlungErstellt, zahlungsauftrag.getDatumGeneriert(), stichtagKorrekturen);
		for (VerfuegungZeitabschnitt zeitabschnitt : verfuegungsZeitabschnitte) {
			if (zeitabschnitt.getZahlungsstatus().isIgnorierend() || zeitabschnitt.getZahlungsstatus().isNeu()) {
				createZahlungspositionenKorrekturUndNachzahlung(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Zahlungsauftrag generiert: ").append(zahlungsauftrag.getGueltigkeit().toRangeString());
		if (isRepetition) {
			sb.append(" [Repetition]");
		}
		LOGGER.info(sb.toString());
		return persistence.merge(zahlungsauftrag);
	}

	/**
	 * Ermittelt die aktuell gueltigen Verfuegungszeitabschnitte fuer die normale monatliche Zahlung (keine Korrekturen).
	 */
	@Nonnull
	private Collection<VerfuegungZeitabschnitt> getGueltigeVerfuegungZeitabschnitte(@Nonnull LocalDate zeitabschnittVon, @Nonnull LocalDate zeitabschnittBis) {
		Objects.requireNonNull(zeitabschnittVon, "zeitabschnittVon muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittBis, "zeitabschnittBis muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = cb.createQuery(VerfuegungZeitabschnitt.class);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<Verfuegung, Betreuung> joinBetreuung = root.join(VerfuegungZeitabschnitt_.verfuegung).join(Verfuegung_.betreuung);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Datum Von
		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), zeitabschnittBis);
		predicates.add(predicateStart);
		// Datum Bis
		Predicate predicateEnd = cb.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), zeitabschnittVon);
		predicates.add(predicateEnd);
		// Nur Angebot KITA
		Predicate predicateAngebot = cb.equal(joinBetreuung.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.KITA);
		predicates.add(predicateAngebot);
		// Nur neueste Verfuegung jedes Falls beachten
		Optional<Gesuchsperiode> gesuchsperiodeAm = gesuchsperiodeService.getGesuchsperiodeAm(zeitabschnittBis);
		if (gesuchsperiodeAm.isPresent()) {
			List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(gesuchsperiodeAm.get());
			if (!gesuchIdsOfAktuellerAntrag.isEmpty()) {
				Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
				predicates.add(predicateAktuellesGesuch);
			} else {
				return Collections.emptyList();
			}
		} else {
			throw new EbeguRuntimeException("getGueltigeVerfuegungZeitabschnitte", "Keine Gesuchsperiode gefunden fuer Stichtag " + Constants.DATE_FORMATTER.format(zeitabschnittBis));
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	/**
	 * Ermittelt alle Zeitabschnitte, welche zu Antraegen gehoeren, die seit dem letzten Zahlungslauf verfuegt wurden.
	 */
	@Nonnull
	private Collection<VerfuegungZeitabschnitt> getVerfuegungsZeitabschnitteNachVerfuegungDatum(@Nonnull LocalDateTime datumVerfuegtVon, @Nonnull LocalDateTime datumVerfuegtBis, @Nonnull LocalDate zeitabschnittBis) {
		Objects.requireNonNull(datumVerfuegtVon, "datumVerfuegtVon muss gesetzt sein");
		Objects.requireNonNull(datumVerfuegtBis, "datumVerfuegtBis muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittBis, "zeitabschnittBis muss gesetzt sein");

		LOGGER.info("Ermittle Korrekturzahlungen:");
		LOGGER.info("Zeitabschnitt endet vor: " + Constants.DATE_FORMATTER.format(zeitabschnittBis));
		LOGGER.info("Gesuch verfuegt zwischen: " + Constants.DATE_FORMATTER.format(datumVerfuegtVon) + " - " + Constants.DATE_FORMATTER.format(datumVerfuegtBis));

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = cb.createQuery(VerfuegungZeitabschnitt.class);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<Verfuegung, Betreuung> joinBetreuung = root.join(VerfuegungZeitabschnitt_.verfuegung).join(Verfuegung_.betreuung);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Datum Bis muss VOR dem regulaeren Auszahlungszeitraum sein (sonst ist es keine Korrektur und schon im obigen Statement enthalten)
		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), zeitabschnittBis);
		predicates.add(predicateStart);
		// Nur Angebot KITA
		Predicate predicateAngebot = cb.equal(joinBetreuung.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.KITA);
		predicates.add(predicateAngebot);
		// Gesuche, welche seit dem letzten Zahlungslauf verfuegt wurden. Nur neueste Verfuegung jedes Falls beachten
		List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(datumVerfuegtVon, datumVerfuegtBis);
		if (!gesuchIdsOfAktuellerAntrag.isEmpty()) {
			Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
			predicates.add(predicateAktuellesGesuch);
		} else {
			return Collections.emptyList();
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	/**
	 * Erstellt eine Zahlungsposition fuer den uebergebenen Zeitabschnitt. Normalfall bei "Erstbuchung"
	 */
	@Nonnull
	private Zahlungsposition createZahlungsposition(@Nonnull VerfuegungZeitabschnitt zeitabschnitt, @Nonnull Zahlungsauftrag zahlungsauftrag, @Nonnull Map<String, Zahlung> zahlungProInstitution) {
		Zahlungsposition zahlungsposition = new Zahlungsposition();
		zahlungsposition.setVerfuegungZeitabschnitt(zeitabschnitt);
		zahlungsposition.setBetrag(zeitabschnitt.getVerguenstigung());
		zahlungsposition.setStatus(ZahlungspositionStatus.NORMAL);
		Zahlung zahlung = findZahlungForInstitution(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
		zahlungsposition.setZahlung(zahlung);
		zahlung.getZahlungspositionen().add(zahlungsposition);
		zeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET);
		return zahlungsposition;
	}

	/**
	 * Erstellt alle notwendigen Zahlungspositionen fuer die Korrektur des uebergebenen Zeitabschnitts.
	 * Bisherige Positionen werden in Abzug gebracht und die neuen hinzugefuegt
	 */
	private void createZahlungspositionenKorrekturUndNachzahlung(@Nonnull VerfuegungZeitabschnitt zeitabschnittNeu, @Nonnull Zahlungsauftrag zahlungsauftrag, @Nonnull Map<String, Zahlung> zahlungProInstitution) {
		// Ermitteln, ob die Vollkosten geaendert haben, seit der letzten Verfuegung, die auch verrechnet wurde!
		List<VerfuegungZeitabschnitt> zeitabschnittOnVorgaengerVerfuegung = verfuegungService
			.findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu, zeitabschnittNeu.getVerfuegung().getBetreuung());
		if (!zeitabschnittOnVorgaengerVerfuegung.isEmpty()) { // Korrekturen
			BigDecimal vollkostenVorgaenger = BigDecimal.ZERO;
			BigDecimal elternbeitragVorgaenger = BigDecimal.ZERO;
			// Eventuell gab es fuer diesen Zeitabschnitt in der Vorgaengerverfuegung mehrere Abschnitte:
			// Total der Vollkosten bzw. Elternbeitraege zusammenzaehlen
			for (VerfuegungZeitabschnitt zeitabschnittBisher : zeitabschnittOnVorgaengerVerfuegung) {
				vollkostenVorgaenger = MathUtil.DEFAULT.add(vollkostenVorgaenger, zeitabschnittBisher.getVollkosten());
				elternbeitragVorgaenger = MathUtil.DEFAULT.add(elternbeitragVorgaenger, zeitabschnittBisher.getElternbeitrag());
			}
			// Und ermitteln, ob es sich im Vergleich nur neuen Verfuegung geaendert hat
			// Falls keine Aenderung -> Keine KorrekturZahlung notwendig!
			boolean vollkostenChanged = vollkostenVorgaenger != null && vollkostenVorgaenger.compareTo(zeitabschnittNeu.getVollkosten()) != 0;
			boolean elternbeitragChanged = elternbeitragVorgaenger != null && elternbeitragVorgaenger.compareTo(zeitabschnittNeu.getElternbeitrag()) != 0;
			if (vollkostenChanged || elternbeitragChanged) {
				// Irgendetwas hat geaendert, wir brauchen eine Korrekturzahlung
				Zahlung zahlung = findZahlungForInstitution(zeitabschnittNeu, zahlungsauftrag, zahlungProInstitution);
				createZahlungspositionKorrekturNeuerWert(zeitabschnittNeu, zahlung, vollkostenChanged);
				for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnittOnVorgaengerVerfuegung) {
					// Fuer die "alten" Verfuegungszeitabschnitte muessen Korrekturbuchungen erstellt werden
					// Wenn die neuen Zeitabschnitte ignoriert sind, setzen wir die alten Zeitabschnitte auch als ignoriert
					createZahlungspositionKorrekturAlterWert(verfuegungZeitabschnitt, zahlung, vollkostenChanged,
						zeitabschnittNeu.getZahlungsstatus().isIgnoriertIgnorierend());
				}
			}
		} else { // Nachzahlungen bzw. Erstgesuche die rueckwirkend ausbezahlt werden muessen
			createZahlungsposition(zeitabschnittNeu, zahlungsauftrag, zahlungProInstitution);
		}
	}

	/**
	 * Erstellt eine Zahlungsposition fuer eine Korrekturzahlung mit dem *neu gueltigen* Wert
	 */
	private void createZahlungspositionKorrekturNeuerWert(@Nonnull VerfuegungZeitabschnitt zeitabschnitt, @Nonnull Zahlung zahlung, boolean vollkostenChanged) {
		Zahlungsposition zahlungsposition = new Zahlungsposition();
		zahlungsposition.setVerfuegungZeitabschnitt(zeitabschnitt);
		zahlungsposition.setBetrag(zeitabschnitt.getVerguenstigung());
		zahlungsposition.setZahlung(zahlung);
		zahlungsposition.setIgnoriert(zeitabschnitt.getZahlungsstatus().isIgnoriertIgnorierend());
		ZahlungspositionStatus status = vollkostenChanged ? ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN : ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG;
		zahlungsposition.setStatus(status);
		if (!zeitabschnitt.getZahlungsstatus().isIgnoriertIgnorierend()) {
			zeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET);
		}
		else if (zeitabschnitt.getZahlungsstatus().isIgnorierend()) {
			zeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.IGNORIERT);
		}
		zahlung.getZahlungspositionen().add(zahlungsposition);
	}

	/**
	 * Erstellt eine Zahlungsposition fuer eine Korrekturzahlung mit der Korrektur des *alten Wertes* (negiert)
	 */
	private void createZahlungspositionKorrekturAlterWert(@Nonnull VerfuegungZeitabschnitt verfuegungZeitabschnitt, @Nonnull Zahlung zahlung, boolean vollkostenChanged,
														  boolean ignoriert) {
		Zahlungsposition korrekturPosition = new Zahlungsposition();
		korrekturPosition.setVerfuegungZeitabschnitt(verfuegungZeitabschnitt);
		korrekturPosition.setBetrag(verfuegungZeitabschnitt.getVerguenstigung().negate());
		korrekturPosition.setZahlung(zahlung);
		korrekturPosition.setIgnoriert(ignoriert); // ignoriert kommt vom neuen Zeitabschnitt
		ZahlungspositionStatus status = vollkostenChanged ? ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN : ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG;
		korrekturPosition.setStatus(status);
		zahlung.getZahlungspositionen().add(korrekturPosition);
	}

	/**
	 * Ermittelt das Zahlungsobjekt fuer die Institution des uebergebenen Zeitabschnitts. Falls im uebergebenen Auftrag schon eine Zahlung
	 * fuer diese Institution vorhanden ist, wird diese zurueckgegeben, ansonsten eine neue erstellt.
	 */
	@Nonnull
	private Zahlung findZahlungForInstitution(@Nonnull VerfuegungZeitabschnitt zeitabschnitt, @Nonnull Zahlungsauftrag zahlungsauftrag, @Nonnull Map<String, Zahlung> zahlungProInstitution) {
		InstitutionStammdaten institution = zeitabschnitt.getVerfuegung().getBetreuung().getInstitutionStammdaten();
		if (zahlungProInstitution.containsKey(institution.getId())) {
			return zahlungProInstitution.get(institution.getId());
		} else {
			Zahlung zahlung = createZahlung(institution, zahlungsauftrag);
			zahlungProInstitution.put(institution.getId(), zahlung);
			return zahlung;
		}
	}

	/**
	 * Erstellt eine Zahlung fuer eine bestimmte Institution, welche zum uebergebenen Auftrag gehoert
	 */
	@Nonnull
	private Zahlung createZahlung(@Nonnull InstitutionStammdaten institution, @Nonnull Zahlungsauftrag zahlungsauftrag) {
		Zahlung zahlung = new Zahlung();
		zahlung.setStatus(ZahlungStatus.ENTWURF);
		zahlung.setInstitutionStammdaten(institution);
		zahlung.setZahlungsauftrag(zahlungsauftrag);
		zahlungsauftrag.getZahlungen().add(zahlung);
		return zahlung;
	}

	/**
	 * Ermittelt den zuletzt durchgefuehrten Zahlungsauftrag
	 */
	@Override
	@Nonnull
	public Optional<Zahlungsauftrag> findLastZahlungsauftrag() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Zahlungsauftrag> query = cb.createQuery(Zahlungsauftrag.class);
		Root<Zahlungsauftrag> root = query.from(Zahlungsauftrag.class);
		query.orderBy(cb.desc(root.get(Zahlungsauftrag_.timestampErstellt)));
		List<Zahlungsauftrag> criteriaResults = persistence.getCriteriaResults(query, 1);
		if (!criteriaResults.isEmpty()) {
			return Optional.of(criteriaResults.get(0));
		}
		return Optional.empty();
	}

	@Override
	@Nonnull
	public Zahlungsauftrag zahlungsauftragAktualisieren(@Nonnull String auftragId, @Nonnull LocalDate datumFaelligkeit, @Nonnull String beschreibung) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Objects.requireNonNull(datumFaelligkeit, "datumFaelligkeit muss gesetzt sein");
		Objects.requireNonNull(beschreibung, "beschreibung muss gesetzt sein");

		Optional<Zahlungsauftrag> auftragOptional = findZahlungsauftrag(auftragId);
		if (auftragOptional.isPresent()) {
			Zahlungsauftrag auftrag = auftragOptional.get();
			// Auftrag kann nur im Status ENTWURF veraendert werden
			if (auftrag.getStatus().isEntwurf()) {
				auftrag.setBeschrieb(beschreibung);
				auftrag.setDatumFaellig(datumFaelligkeit);
				return persistence.merge(auftrag);
			} else {
				throw new IllegalStateException("Auftrag kann nicht mehr veraendert werden: " + auftragId);
			}
		} else {
			throw new EbeguEntityNotFoundException("zahlungsauftragAktualisieren", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId);
		}
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN})
	public Zahlungsauftrag zahlungsauftragAusloesen(@Nonnull String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		zahlungsauftrag.setStatus(ZahlungauftragStatus.AUSGELOEST);
		for (Zahlung zahlung : zahlungsauftrag.getZahlungen()) {
			if (!ZahlungStatus.ENTWURF.equals(zahlung.getStatus())) {
				throw new IllegalArgumentException("Zahlung muss im Status ENTWURF sein, wenn der Auftrag ausgelöst wird: " + zahlung.getId());
			}
			zahlung.setStatus(ZahlungStatus.AUSGELOEST);
			persistence.merge(zahlung);
		}
		return persistence.merge(zahlungsauftrag);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
	public Optional<Zahlungsauftrag> findZahlungsauftrag(@Nonnull String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		return Optional.ofNullable(zahlungsauftrag);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
	public Optional<Zahlung> findZahlung(@Nonnull String zahlungId) {
		Objects.requireNonNull(zahlungId, "zahlungId muss gesetzt sein");
		Zahlung zahlung = persistence.find(Zahlung.class, zahlungId);
		return Optional.ofNullable(zahlung);
	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public void deleteZahlungsauftrag(@Nonnull String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Optional<Zahlungsauftrag> auftragOptional = findZahlungsauftrag(auftragId);
		if (auftragOptional.isPresent()) {
			// Alle verknuepften Zeitabschnitte wieder auf "unbezahlt" setzen
			Zahlungsauftrag auftrag = auftragOptional.get();
			for (Zahlung zahlung : auftrag.getZahlungen()) {
				for (Zahlungsposition zahlungsposition : zahlung.getZahlungspositionen()) {
					if (zahlungsposition.getVerfuegungZeitabschnitt().getZahlungsstatus().isVerrechnet()) {
						zahlungsposition.getVerfuegungZeitabschnitt().setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.NEU);
						persistence.merge(zahlungsposition.getVerfuegungZeitabschnitt());
					}
				}
			}
		}
		// Dann erst den Auftrag loeschen
		Zahlungsauftrag auftragToRemove = auftragOptional.orElseThrow(() -> new EbeguEntityNotFoundException("deleteZahlungsauftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId));
		persistence.remove(auftragToRemove);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, JURIST, REVISOR})
	public Collection<Zahlungsauftrag> getAllZahlungsauftraege() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Zahlungsauftrag.class));
	}


	@Override
	@Nonnull
	@RolesAllowed(value = {SUPER_ADMIN, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public Zahlung zahlungBestaetigen(@Nonnull String zahlungId) {
		Objects.requireNonNull(zahlungId, "zahlungId muss gesetzt sein");
		Zahlung zahlung = persistence.find(Zahlung.class, zahlungId);
		zahlung.setStatus(ZahlungStatus.BESTAETIGT);
		Zahlung persistedZahlung = persistence.merge(zahlung);
		zahlungauftragBestaetigenIfAllZahlungenBestaetigt(zahlung.getZahlungsauftrag());
		return persistedZahlung;
	}

	@Override
	public Collection<Zahlungsauftrag> getZahlungsauftraegeInPeriode(LocalDate von, @Nonnull LocalDate bis) {

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Zahlungsauftrag> query = cb.createQuery(Zahlungsauftrag.class);

		Root<Zahlungsauftrag> root = query.from(Zahlungsauftrag.class);
		Predicate predicates = cb.between(root.get(Zahlungsauftrag_.datumGeneriert), cb.literal(von.atStartOfDay()), cb.literal(bis.atTime(LocalTime.MAX)));

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);

	}

	@Override
	@RolesAllowed(value = {SUPER_ADMIN})
	public void deleteZahlungspositionenOfGesuch(@Nonnull Gesuch gesuch) {
		Objects.requireNonNull(gesuch, "gesuch muss gesetzt sein");
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Zahlungsposition> query = cb.createQuery(Zahlungsposition.class);

		Root<Zahlungsposition> root = query.from(Zahlungsposition.class);
		Predicate predicates = cb.equal(root.get(Zahlungsposition_.verfuegungZeitabschnitt)
			.get(VerfuegungZeitabschnitt_.verfuegung)
			.get(Verfuegung_.betreuung)
			.get(Betreuung_.kind)
			.get(KindContainer_.gesuch), gesuch);

		query.where(predicates);
		List<Zahlungsposition> zahlungspositionList = persistence.getCriteriaResults(query);
		for (Zahlungsposition zahlungsposition : zahlungspositionList) {
			persistence.remove(Zahlungsposition.class, zahlungsposition.getId());
		}
	}

	@Nonnull
	private Zahlungsauftrag zahlungauftragBestaetigenIfAllZahlungenBestaetigt(@Nonnull Zahlungsauftrag zahlungsauftrag) {
		Objects.requireNonNull(zahlungsauftrag, "zahlungsauftrag darf nicht null sein");
		if (zahlungsauftrag.getZahlungen().stream().allMatch(zahlung -> zahlung.getStatus().equals(ZahlungStatus.BESTAETIGT))) {
			zahlungsauftrag.setStatus(ZahlungauftragStatus.BESTAETIGT);
			return persistence.merge(zahlungsauftrag);
		}
		return zahlungsauftrag;
	}

}


