package ch.dvbern.ebegu.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Mahnung_;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.ebegu.rules.anlageverzeichnis.DokumentenverzeichnisEvaluator;
import ch.dvbern.ebegu.util.DokumenteUtil;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Mahnungen
 */
@SuppressWarnings("OverlyBroadCatchBlock")
@Stateless
@Local(MahnungService.class)
@PermitAll
public class MahnungServiceBean extends AbstractBaseService implements MahnungService {

	private static final Logger LOG = LoggerFactory.getLogger(MahnungServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Mahnung> persistence;

	@Inject
	private DokumentGrundService dokumentGrundService;

	@Inject
	private DokumentenverzeichnisEvaluator dokumentenverzeichnisEvaluator;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private MailService mailService;

	@Inject
	private GeneratedDokumentService generatedDokumentService;


	@Override
	@Nonnull
	public Mahnung createMahnung(@Nonnull Mahnung mahnung) {
		Objects.requireNonNull(mahnung);
		// Sicherstellen, dass keine offene Mahnung desselben Typs schon existiert
		assertNoOpenMahnungOfType(mahnung.getGesuch(), mahnung.getMahnungTyp());
		if (MahnungTyp.ZWEITE_MAHNUNG == mahnung.getMahnungTyp()) {
			// Die Erst-Mahnung suchen und verknuepfen, wird im Dokument gebraucht
			Optional<Mahnung> erstMahnung = findAktiveErstMahnung(mahnung.getGesuch());
			if (erstMahnung.isPresent()) {
				mahnung.setVorgaengerId(erstMahnung.get().getId());
			} else {
				throw new EbeguRuntimeException("createMahnung", "Zweitmahnung erstellt ohne aktive Erstmahnung! " + mahnung.getId(), mahnung.getId());
			}
		}
		Mahnung persistedMahnung = persistence.persist(mahnung);
		Gesuch gesuch = persistedMahnung.getGesuch();
		// Das Mahnungsdokument drucken
		try {
			generatedDokumentService.getMahnungDokumentAccessTokenGeneratedDokument(mahnung, true);
		} catch (MimeTypeParseException | IOException | MergeDocException e) {
			throw new EbeguRuntimeException("createMahnung", "Mahnung-Dokument konnte nicht erstellt werden " +
				mahnung.getId(), e, mahnung.getId());
		}
		// Mail senden
		try {
			mailService.sendInfoMahnung(gesuch);
		} catch (Exception e) {
			LOG.error("Mail InfoMahnung konnte nicht verschickt werden fuer Gesuch " + gesuch.getId(), e);
		}
		return persistedMahnung;
	}

	@Override
	@Nonnull
	public Optional<Mahnung> findMahnung(@Nonnull String mahnungId) {
		Objects.requireNonNull(mahnungId, "mahnungId muss gesetzt sein");
		Mahnung mahnung = persistence.find(Mahnung.class, mahnungId);
		if (mahnung != null) {
			authorizer.checkReadAuthorization(mahnung.getGesuch());
		}
		return Optional.ofNullable(mahnung);
	}

	@Override
	@Nonnull
	public Collection<Mahnung> findMahnungenForGesuch(@Nonnull Gesuch gesuch) {
		authorizer.checkReadAuthorization(gesuch);
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
			mahnung.setTimestampAbgeschlossen(LocalDateTime.now());
			persistence.persist(mahnung);
		}
	}

	@Override
	@Nonnull
	public String getInitialeBemerkungen(@Nonnull Gesuch gesuch) {
		authorizer.checkReadAuthorization(gesuch);
		List<DokumentGrund> dokumentGrundsMerged = new ArrayList<>();
		dokumentGrundsMerged.addAll(DokumenteUtil
			.mergeNeededAndPersisted(dokumentenverzeichnisEvaluator.calculate(gesuch),
				dokumentGrundService.findAllDokumentGrundByGesuch(gesuch), gesuch));
		Collections.sort(dokumentGrundsMerged);

		StringBuilder bemerkungenBuilder = new StringBuilder();
		for (DokumentGrund dokumentGrund : dokumentGrundsMerged) {
			StringBuilder dokumentData = PrintUtil.parseDokumentGrundDataToString(dokumentGrund);
			if (dokumentData.length() > 0) {
				bemerkungenBuilder.append(dokumentData);
				bemerkungenBuilder.append('\n');
			}
		}
		return bemerkungenBuilder.toString();
	}

	@Override
	public void fristAblaufTimer() {
		// Es muessen alle ueberprueft werden, die noch aktiv sind und deren Ablaufdatum < NOW liegt
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mahnung> query = cb.createQuery(Mahnung.class);
		Root<Mahnung> root = query.from(Mahnung.class);
		query.distinct(true);

		Predicate predicateAktiv = cb.isNull(root.get(Mahnung_.timestampAbgeschlossen));
		Predicate predicateNochNichtAbgelaufenMarkiert = cb.isFalse(root.get(Mahnung_.abgelaufen));
		Predicate predicateAbgelaufen = cb.lessThan(root.get(Mahnung_.datumFristablauf), LocalDate.now());
		query.where(predicateAktiv, predicateNochNichtAbgelaufenMarkiert, predicateAbgelaufen);

		List<Mahnung> gesucheMitAbgelaufenenMahnungen = persistence.getCriteriaResults(query);
		for (Mahnung mahnung : gesucheMitAbgelaufenenMahnungen) {
			final Gesuch gesuch = mahnung.getGesuch();
			if (AntragStatus.ERSTE_MAHNUNG == gesuch.getStatus() || AntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN == gesuch.getStatus()) {
				gesuch.setStatus(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN);
				gesuchService.updateGesuch(gesuch, true);
			} else if (AntragStatus.ZWEITE_MAHNUNG == gesuch.getStatus() || AntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN == gesuch.getStatus()) {
				gesuch.setStatus(AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN);
				gesuchService.updateGesuch(gesuch, true);
			}
			mahnung.setAbgelaufen(true);
			persistence.merge(mahnung);
		}
	}

	@Override
	@Nonnull
	public  Optional<Mahnung> findAktiveErstMahnung(Gesuch gesuch) {
		authorizer.checkReadAuthorization(gesuch);
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mahnung> query = cb.createQuery(Mahnung.class);
		Root<Mahnung> root = query.from(Mahnung.class);
		query.select(root);
		Predicate predicateTyp = cb.equal(root.get(Mahnung_.mahnungTyp), MahnungTyp.ERSTE_MAHNUNG);
		Predicate predicateAktiv = cb.isNull(root.get(Mahnung_.timestampAbgeschlossen));
		Predicate predicateGesuch = cb.equal(root.get(Mahnung_.gesuch), gesuch);
		query.where(predicateTyp, predicateAktiv, predicateGesuch);
		// Wirft eine NonUnique-Exception, falls mehrere aktive ErstMahnungen!
		Mahnung aktiveErstMahnung = persistence.getCriteriaSingleResult(query);
		return Optional.ofNullable(aktiveErstMahnung);
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public void removeAllMahnungenFromGesuch(Gesuch gesuch) {
		Collection<Mahnung> mahnungenFromGesuch = findMahnungenForGesuch(gesuch);
		for (Mahnung mahnung : mahnungenFromGesuch) {
			persistence.remove(Mahnung.class, mahnung.getId());
		}
	}

	private  void assertNoOpenMahnungOfType(@Nonnull Gesuch gesuch, @Nonnull MahnungTyp mahnungTyp) {
		authorizer.checkReadAuthorization(gesuch);
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Mahnung> query = cb.createQuery(Mahnung.class);
		Root<Mahnung> root = query.from(Mahnung.class);
		query.select(root);
		Predicate predicateTyp = cb.equal(root.get(Mahnung_.mahnungTyp), mahnungTyp);
		Predicate predicateAktiv = cb.isNull(root.get(Mahnung_.timestampAbgeschlossen));
		Predicate predicateGesuch = cb.equal(root.get(Mahnung_.gesuch), gesuch);
		query.where(predicateTyp, predicateAktiv, predicateGesuch);
		// Wirft eine NonUnique-Exception, falls mehrere aktive ErstMahnungen!
		List<Mahnung> criteriaResults = persistence.getCriteriaResults(query);
		if (!criteriaResults.isEmpty()) {
			throw new EbeguRuntimeException("assertNoOpenMahnungOfType", ErrorCodeEnum.ERROR_TOO_MANY_RESULTS);
		}
	}
}
