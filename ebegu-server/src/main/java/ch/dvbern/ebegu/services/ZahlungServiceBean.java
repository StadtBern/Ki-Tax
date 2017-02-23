package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


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
@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
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
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragErstellen(LocalDate datumFaelligkeit, String beschreibung) {
		return zahlungsauftragErstellen(datumFaelligkeit, beschreibung, LocalDateTime.now());
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragErstellen(LocalDate datumFaelligkeit, String beschreibung, LocalDateTime datumGeneriert) {
		// Es darf immer nur ein Zahlungsauftrag im Status ENTWURF sein
		Optional<Zahlungsauftrag> lastZahlungsauftrag = findLastZahlungsauftrag();
		if (lastZahlungsauftrag.isPresent() && lastZahlungsauftrag.get().getStatus().isEntwurf()) {
			throw new EbeguRuntimeException("zahlungsauftragErstellen", ErrorCodeEnum.ERROR_ZAHLUNG_ERSTELLEN, "Es darf kein neuer Entwurf erstellt werden, bevor der letzte Auftrag freigegeben wurde");
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
		LocalDate zeitabschnittVon = null;
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
		LocalDate stichtagKorrekturen = isRepetition ? zeitabschnittVon.plusMonths(1) : zeitabschnittVon;
		Collection<VerfuegungZeitabschnitt> mutationsVerfuegungsZeitabschnitte = getMutationsVerfuegungsZeitabschnitte(lastZahlungErstellt, zahlungsauftrag.getDatumGeneriert(), stichtagKorrekturen);
		for (VerfuegungZeitabschnitt zeitabschnitt : mutationsVerfuegungsZeitabschnitte) {
			if (zeitabschnitt.getZahlungsstatus().isNeu()) {
				createZahlungspositionenKorrektur(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
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
	private Collection<VerfuegungZeitabschnitt> getGueltigeVerfuegungZeitabschnitte(LocalDate zeitabschnittVon, LocalDate zeitabschnittBis) {
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
			Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
			predicates.add(predicateAktuellesGesuch);
		} else {
			throw new EbeguRuntimeException("getGueltigeVerfuegungZeitabschnitte", "Keine Gesuchsperiode gefunden fuer Stichtag " + Constants.DATE_FORMATTER.format(zeitabschnittBis));
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	/**
	 * Ermittelt alle Zeitabschnitte, welche zu Antraegen gehoeren, die seit dem letzten Zahlungslauf verfuegt wurden.
	 */
	private Collection<VerfuegungZeitabschnitt> getMutationsVerfuegungsZeitabschnitte(LocalDateTime datumVerfuegtVon, LocalDateTime datumVerfuegtBis, LocalDate zeitabschnittVon) {
		Objects.requireNonNull(datumVerfuegtVon, "datumVerfuegtVon muss gesetzt sein");
		Objects.requireNonNull(datumVerfuegtBis, "datumVerfuegtBis muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittVon, "zeitabschnittVon muss gesetzt sein");

		LOGGER.info("Ermittle Korrekturzahlungen:");
		LOGGER.info("Zeitabschnitt endet vor: " + Constants.DATE_FORMATTER.format(zeitabschnittVon.minusDays(1)));
		LOGGER.info("Gesuch verfuegt zwischen: " + Constants.DATE_FORMATTER.format(datumVerfuegtVon) + " - " + Constants.DATE_FORMATTER.format(datumVerfuegtBis));

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = cb.createQuery(VerfuegungZeitabschnitt.class);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<Verfuegung, Betreuung> joinBetreuung = root.join(VerfuegungZeitabschnitt_.verfuegung).join(Verfuegung_.betreuung);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Datum Bis muss VOR dem regulaeren Auszahlungszeitraum sein (sonst ist es keine Korrektur und schon im obigen Statement enthalten)
		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), zeitabschnittVon.minusDays(1));
		predicates.add(predicateStart);
		// Nur Angebot KITA
		Predicate predicateAngebot = cb.equal(joinBetreuung.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.KITA);
		predicates.add(predicateAngebot);
		// Gesuche, welche seit dem letzten Zahlungslauf verfuegt wurden. Nur neueste Verfuegung jedes Falls beachten
		List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(datumVerfuegtVon, datumVerfuegtBis);
		Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
		predicates.add(predicateAktuellesGesuch);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	/**
	 * Erstellt eine Zahlungsposition fuer den uebergebenen Zeitabschnitt. Normalfall bei "Erstbuchung"
	 */
	private Zahlungsposition createZahlungsposition(VerfuegungZeitabschnitt zeitabschnitt, Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
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
	private void createZahlungspositionenKorrektur(VerfuegungZeitabschnitt zeitabschnittNeu, Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
		// Ermitteln, ob die Vollkosten geaendert haben, seit der letzten Verfuegung, die auch verrechnet wurde!
		List<VerfuegungZeitabschnitt> zeitabschnittOnVorgaengerVerfuegung = findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu);
		if (!zeitabschnittOnVorgaengerVerfuegung.isEmpty()) {
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
			if (!vollkostenChanged && !elternbeitragChanged) {
				zeitabschnittNeu.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.IDENTISCH);
			} else {
				// Irgendetwas hat geaendert, wir brauchen eine Korrekturzahlung
				Zahlung zahlung = findZahlungForInstitution(zeitabschnittNeu, zahlungsauftrag, zahlungProInstitution);
				createZahlungspositionKorrekturNeuerWert(zeitabschnittNeu, zahlung, vollkostenChanged);
				for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnittOnVorgaengerVerfuegung) {
					// Fuer die "alten" Verfuegungszeitabschnitte muessen Korrekturbuchungen erstellt werden
					createZahlungspositionKorrekturAlterWert(verfuegungZeitabschnitt, zahlung, vollkostenChanged);
				}
			}
		}
	}

	/**
	 * Erstellt eine Zahlungsposition fuer eine Korrekturzahlung mit dem *neu gueltigen* Wert
	 */
	private void createZahlungspositionKorrekturNeuerWert(VerfuegungZeitabschnitt zeitabschnitt, Zahlung zahlung, boolean vollkostenChanged) {
		Zahlungsposition zahlungsposition = new Zahlungsposition();
		zahlungsposition.setVerfuegungZeitabschnitt(zeitabschnitt);
		zahlungsposition.setBetrag(zeitabschnitt.getVerguenstigung());
		zahlungsposition.setZahlung(zahlung);
		zahlungsposition.setIgnoriert(zeitabschnitt.getZahlungsstatus().isIgnoriert());
		ZahlungspositionStatus status = vollkostenChanged ? ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN : ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG;
		zahlungsposition.setStatus(status);
		if (!zeitabschnitt.getZahlungsstatus().isIgnoriert()) { // Den Status nur ueberschreiben, wenn nicht ignoriert
			zeitabschnitt.setZahlungsstatus(VerfuegungsZeitabschnittZahlungsstatus.VERRECHNET);
		}
		zahlung.getZahlungspositionen().add(zahlungsposition);
	}

	/**
	 * Erstellt eine Zahlungsposition fuer eine Korrekturzahlung mit der Korrektur des *alten Wertes* (negiert)
	 */
	private void createZahlungspositionKorrekturAlterWert(VerfuegungZeitabschnitt verfuegungZeitabschnitt, Zahlung zahlung, boolean vollkostenChanged) {
		Zahlungsposition korrekturPosition = new Zahlungsposition();
		korrekturPosition.setVerfuegungZeitabschnitt(verfuegungZeitabschnitt);
		korrekturPosition.setBetrag(verfuegungZeitabschnitt.getVerguenstigung().negate());
		korrekturPosition.setZahlung(zahlung);
		korrekturPosition.setIgnoriert(verfuegungZeitabschnitt.getZahlungsstatus().isIgnoriert());
		ZahlungspositionStatus status = vollkostenChanged ? ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN : ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG;
		korrekturPosition.setStatus(status);
		zahlung.getZahlungspositionen().add(korrekturPosition);
	}

	private List<VerfuegungZeitabschnitt> findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(VerfuegungZeitabschnitt zeitabschnittNeu) {
		return findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu, zeitabschnittNeu.getVerfuegung().getBetreuung());
	}

	private List<VerfuegungZeitabschnitt> findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(VerfuegungZeitabschnitt zeitabschnittNeu, Betreuung betreuungNeu) {
		Optional<Verfuegung> vorgaengerVerfuegung = verfuegungService.findVorgaengerVerfuegung(betreuungNeu);
		if (vorgaengerVerfuegung.isPresent()) {
			List<VerfuegungZeitabschnitt> zeitabschnittOnVorgaengerVerfuegung = findZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu.getGueltigkeit(), vorgaengerVerfuegung.get());
			Betreuung vorgaengerBetreuung = null;
			for (VerfuegungZeitabschnitt zeitabschnitt : zeitabschnittOnVorgaengerVerfuegung) {
				vorgaengerBetreuung = zeitabschnitt.getVerfuegung().getBetreuung();
				if (zeitabschnitt.getZahlungsstatus().isVerrechnet()) {
					return zeitabschnittOnVorgaengerVerfuegung;
				}
			}
			// Es gab keine bereits Verrechneten Zeitabschnitte auf dieser Verfuegung -> eins weiter zurueckgehen
			return findVerrechnetenZeitabschnittOnVorgaengerVerfuegung(zeitabschnittNeu, vorgaengerBetreuung);
		}
		return null;
	}

	/**
	 * Findet das anspruchberechtigtes Pensum zum Zeitpunkt des neuen Zeitabschnitt-Start
	 */
	private List<VerfuegungZeitabschnitt> findZeitabschnittOnVorgaengerVerfuegung(DateRange newVerfuegungGueltigkeit, Verfuegung lastVerfuegung) {
		List<VerfuegungZeitabschnitt> lastVerfuegungsZeitabschnitte = new ArrayList<>();
		if (lastVerfuegung != null) {
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : lastVerfuegung.getZeitabschnitte()) {
				final DateRange gueltigkeit = verfuegungZeitabschnitt.getGueltigkeit();
				if (gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigAb()) || gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigBis())) {
					lastVerfuegungsZeitabschnitte.add(verfuegungZeitabschnitt);
				}
			}
		}
		return lastVerfuegungsZeitabschnitte;
	}

	/**
	 * Ermittelt das Zahlungsobjekt fuer die Institution des uebergebenen Zeitabschnitts. Falls im uebergebenen Auftrag schon eine Zahlung
	 * fuer diese Institution vorhanden ist, wird diese zurueckgegeben, ansonsten eine neue erstellt.
	 */
	private Zahlung findZahlungForInstitution(VerfuegungZeitabschnitt zeitabschnitt, Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
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
	private Zahlung createZahlung(InstitutionStammdaten institution, Zahlungsauftrag zahlungsauftrag) {
		Zahlung zahlung = new Zahlung();
		zahlung.setStatus(ZahlungStatus.AUSGELOEST);
		zahlung.setInstitutionStammdaten(institution);
		zahlung.setZahlungsauftrag(zahlungsauftrag);
		zahlungsauftrag.getZahlungen().add(zahlung);
		return zahlung;
	}

	/**
	 * Ermittelt den zuletzt durchgefuehrten Zahlungsauftrag
	 */
	private Optional<Zahlungsauftrag> findLastZahlungsauftrag() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Zahlungsauftrag> query = cb.createQuery(Zahlungsauftrag.class);
		Root<Zahlungsauftrag> root = query.from(Zahlungsauftrag.class);
		query.orderBy(cb.desc(root.get(Zahlungsauftrag_.datumGeneriert)));
		List<Zahlungsauftrag> criteriaResults = persistence.getCriteriaResults(query, 1);
		if (!criteriaResults.isEmpty()) {
			return Optional.of(criteriaResults.get(0));
		}
		return Optional.empty();
	}

	@Override
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
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragAusloesen(String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		zahlungsauftrag.setStatus(ZahlungauftragStatus.AUSGELOEST);
		return persistence.merge(zahlungsauftrag);
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Optional<Zahlungsauftrag> findZahlungsauftrag(String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		return Optional.ofNullable(zahlungsauftrag);
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public void deleteZahlungsauftrag(String auftragId) {
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
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Zahlungsauftrag> getAllZahlungsauftraege() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Zahlungsauftrag.class));
	}


	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Zahlung zahlungBestaetigen(String zahlungId) {
		Objects.requireNonNull(zahlungId, "zahlungId muss gesetzt sein");
		Zahlung zahlung = persistence.find(Zahlung.class, zahlungId);
		zahlung.setStatus(ZahlungStatus.BESTAETIGT);
		return persistence.merge(zahlung);
	}
}


