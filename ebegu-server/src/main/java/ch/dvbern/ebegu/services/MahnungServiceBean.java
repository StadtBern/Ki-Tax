package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Mahnung_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.rules.Anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.*;

/**
 * Service fuer Mahnungen
 */
@Stateless
@Local(MahnungService.class)
@PermitAll
public class MahnungServiceBean extends AbstractBaseService implements MahnungService {

	@Inject
	private Persistence<Mahnung> persistence;

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Inject
	private GesuchService gesuchService;


	@Override
	@Nonnull
	public Mahnung createMahnung(@Nonnull Mahnung mahnung) {
		Objects.requireNonNull(mahnung);
		if (MahnungTyp.ZWEITE_MAHNUNG.equals(mahnung.getMahnungTyp())) {
			// Die Erst-Mahnung suchen und verknuepfen, wird im Dokument gebraucht
			Optional<Mahnung> erstMahnung = findAktiveErstMahnung(mahnung.getGesuch());
			if (erstMahnung.isPresent()) {
				mahnung.setVorgaengerId(erstMahnung.get().getId());
			} else {
				throw new EbeguRuntimeException("createMahnung", "Zweitmahnung erstellt ohne aktive Erstmahnung! "+mahnung.getId(), mahnung.getId());
			}
		}
		return persistence.persist(mahnung);
	}

	@Override
	@Nonnull
	public Optional<Mahnung> findMahnung(@Nonnull String mahnungId) {
		Objects.requireNonNull(mahnungId, "mahnungId muss gesetzt sein");
		Mahnung mahnung =  persistence.find(Mahnung.class, mahnungId);
		return Optional.ofNullable(mahnung);
	}

	@Override
	@Nonnull
	public Collection<Mahnung> findMahnungenForGesuch(@Nonnull Gesuch gesuch) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mahnung> query = cb.createQuery(Mahnung.class);
		Root<Mahnung> root = query.from(Mahnung.class);

		Predicate prediateGesuch = cb.equal(root.get(Mahnung_.gesuch), gesuch);
		query.where(prediateGesuch);
		query.orderBy(cb.asc(root.get(Mahnung_.timestampErstellt)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	public void mahnlaufBeenden(@Nonnull Gesuch gesuch) {
		// Alle Mahnungen auf erledigt stellen
		Collection<Mahnung> mahnungenForGesuch = findMahnungenForGesuch(gesuch);
		for (Mahnung mahnung : mahnungenForGesuch) {
			mahnung.setActive(false);
			persistence.persist(mahnung);
		}
	}

	@Override
	@Nonnull
	public String getInitialeBemerkungen(@Nonnull Gesuch gesuch) {
		final Set<DokumentGrund> dokumentGrundsMerged = DokumenteUtil
			.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(gesuch),
				dokumentGrundService.getAllDokumentGrundByGesuch(gesuch));

		StringBuilder bemerkungenBuilder = new StringBuilder();
		for (DokumentGrund dokumentGrund : dokumentGrundsMerged) {
			if (dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
				bemerkungenBuilder.append(ServerMessageUtil.translateEnumValue(dokumentGrund.getDokumentTyp()));
				if (StringUtils.isNotEmpty(dokumentGrund.getFullName())) {
					bemerkungenBuilder.append(" (");
					bemerkungenBuilder.append(dokumentGrund.getFullName());
					bemerkungenBuilder.append(")");
				}
				bemerkungenBuilder.append("\n");
			}
		}
		return bemerkungenBuilder.toString();
	}

	@Override
	public void fristAblaufTimer() {
		// Es muessen alle ueberprueft werden, die noch aktiv sind und deren Ablaufdatum < NOW liegt
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Gesuch> query = cb.createQuery(Gesuch.class);
		Root<Mahnung> root = query.from(Mahnung.class);
		query.distinct(true);

		Predicate predicateAktiv = cb.equal(root.get(Mahnung_.active), Boolean.TRUE);
		Predicate predicateAbgelaufen = cb.lessThanOrEqualTo(root.get(Mahnung_.datumFristablauf), LocalDate.now());
		query.where(predicateAktiv, predicateAbgelaufen);


		query.select(root.get(Mahnung_.gesuch));
		List<Gesuch> gesucheMitAbgelaufenenMahnungen = persistence.getCriteriaResults(query);
		for (Gesuch gesuch : gesucheMitAbgelaufenenMahnungen) {
			if (AntragStatus.ERSTE_MAHNUNG.equals(gesuch.getStatus())) {
				gesuch.setStatus(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN);
			} else if (AntragStatus.ZWEITE_MAHNUNG.equals(gesuch.getStatus())) {
				gesuch.setStatus(AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN);
			} else {
				if (!(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN.equals(gesuch.getStatus()) || AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN.equals(gesuch.getStatus()))) {
					throw new IllegalArgumentException("Mahnung abgelaufen fuer ein Gesuch, welches nicht im Status MAHNUNG war");
				}
			}
			gesuchService.updateGesuch(gesuch, true);
		}
	}

	@Nonnull
	private Optional<Mahnung> findAktiveErstMahnung(Gesuch gesuch) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mahnung> query = cb.createQuery(Mahnung.class);
		Root<Mahnung> root = query.from(Mahnung.class);
		query.select(root);
		Predicate predicateTyp = cb.equal(root.get(Mahnung_.mahnungTyp), MahnungTyp.ERSTE_MAHNUNG);
		Predicate predicateAktiv = cb.equal(root.get(Mahnung_.active), Boolean.TRUE);
		Predicate predicateGesuch = cb.equal(root.get(Mahnung_.gesuch), gesuch);
		query.where(predicateTyp, predicateAktiv, predicateGesuch);
		// Wirft eine NonUnique-Exception, falls mehrere aktive ErstMahnungen!
		Mahnung aktiveErstMahnung = persistence.getCriteriaSingleResult(query);
		return Optional.ofNullable(aktiveErstMahnung);
	}
}
