package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.UploadFileInfo;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeTypeParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * Service fuer EbeguVorlage
 */
@Stateless
@Local(EbeguVorlageService.class)
public class EbeguVorlageServiceBean extends AbstractBaseService implements EbeguVorlageService {

	private final Logger LOGGER = LoggerFactory.getLogger(EbeguVorlageServiceBean.class.getSimpleName());

	@Inject
	private Persistence<EbeguVorlage> persistence;

	@Inject
	private FileSaverService fileSaverService;

	@Inject
	private PrincipalBean principalBean;

	@Nonnull
	@Override
	public EbeguVorlage saveEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage) {
		Objects.requireNonNull(ebeguVorlage);
		return persistence.merge(ebeguVorlage);
	}

	@Override
	@Nonnull
	public Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey) {
		return getEbeguVorlageByDatesAndKey(abDate, bisDate, ebeguVorlageKey, persistence.getEntityManager());
	}

	@Override
	@Nonnull
	public Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey, final EntityManager em) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);


		ParameterExpression<EbeguVorlageKey> keyParam = cb.parameter(EbeguVorlageKey.class, "key");
		Predicate keyPredicate = cb.equal(root.get(EbeguVorlage_.name), keyParam);

		ParameterExpression<LocalDate> dateAbParam = cb.parameter(LocalDate.class, "dateAb");
		Predicate dateAbPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb), dateAbParam);

		ParameterExpression<LocalDate> dateBisParam = cb.parameter(LocalDate.class, "dateBis");
		Predicate dateBisPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis), dateBisParam);

		query.where(keyPredicate, dateAbPredicate, dateBisPredicate);
		TypedQuery<EbeguVorlage> q = em.createQuery(query);
		q.setParameter(dateAbParam, abDate);
		q.setParameter(dateBisParam, bisDate);
		q.setParameter(keyParam, ebeguVorlageKey);
		List<EbeguVorlage> resultList = q.getResultList();
		EbeguVorlage paramOrNull = null;
		if (!resultList.isEmpty() && resultList.size() == 1) {
			paramOrNull = resultList.get(0);
		} else if (resultList.size() > 1) {
			throw new NonUniqueResultException();
		}
		return Optional.ofNullable(paramOrNull);
	}

	@Override
	@Nonnull
	public List<EbeguVorlage> getALLEbeguVorlageByGesuchsperiode(Gesuchsperiode gesuchsperiode) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);

		ParameterExpression<LocalDate> dateAbParam = cb.parameter(LocalDate.class, "dateAb");
		Predicate dateAbPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb), dateAbParam);

		ParameterExpression<LocalDate> dateBisParam = cb.parameter(LocalDate.class, "dateBis");
		Predicate dateBisPredicate = cb.equal(root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis), dateBisParam);

		Predicate proGesuchsperiodePredicate = cb.isTrue(root.get(EbeguVorlage_.proGesuchsperiode));

		query.where(dateAbPredicate, dateBisPredicate, proGesuchsperiodePredicate);
		TypedQuery<EbeguVorlage> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(dateAbParam, gesuchsperiode.getGueltigkeit().getGueltigAb());
		q.setParameter(dateBisParam, gesuchsperiode.getGueltigkeit().getGueltigBis());

		List<EbeguVorlage> resultList = q.getResultList();
		return resultList;
	}

	@Nonnull
	private Optional<EbeguVorlage> getNewestEbeguVorlageByKey(EbeguVorlageKey key) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);

		ParameterExpression<EbeguVorlageKey> nameParam = cb.parameter(EbeguVorlageKey.class, "key");
		Predicate namePredicate = cb.equal(root.get(EbeguVorlage_.name), nameParam);

		query.orderBy(cb.desc(root.get(EbeguVorlage_.timestampErstellt)));
		query.where(namePredicate);
		TypedQuery<EbeguVorlage> q = persistence.getEntityManager().createQuery(query);
		q.setParameter(nameParam, key);

		List<EbeguVorlage> resultList = q.getResultList();
		if (CollectionUtils.isNotEmpty(resultList)) {
			return Optional.of(resultList.get(0));
		}
		return Optional.empty();
	}

	@Override
	@Nullable
	public EbeguVorlage updateEbeguVorlage(@Nonnull EbeguVorlage ebeguVorlage) {
		Objects.requireNonNull(ebeguVorlage);
		return persistence.merge(ebeguVorlage);
	}

	@Override
	public void removeVorlage(@Nonnull String id) {
		Validate.notNull(id);
		Optional<EbeguVorlage> ebeguVorlage = findById(id);
		ebeguVorlage.orElseThrow(() -> new EbeguEntityNotFoundException("removeEbeguVorlage", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, id));
		final EbeguVorlage ebeguVorlageEntity = ebeguVorlage.get();

		fileSaverService.remove(ebeguVorlageEntity.getVorlage().getFilepfad());

		ebeguVorlageEntity.setVorlage(null);
		updateEbeguVorlage(ebeguVorlageEntity);
	}

	@Nonnull
	@Override
	public Optional<EbeguVorlage> findById(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		EbeguVorlage a = persistence.find(EbeguVorlage.class, id);
		return Optional.ofNullable(a);
	}

	@Nonnull
	@Override
	public Collection<EbeguVorlage> getALLEbeguVorlageByDate(@Nonnull LocalDate date, boolean proGesuchsperiode) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<EbeguVorlage> query = cb.createQuery(EbeguVorlage.class);
		Root<EbeguVorlage> root = query.from(EbeguVorlage.class);
		query.select(root);

		ParameterExpression<LocalDate> dateParam = cb.parameter(LocalDate.class, "date");
		Predicate intervalPredicate = cb.between(dateParam,
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigAb),
			root.get(AbstractDateRangedEntity_.gueltigkeit).get(DateRange_.gueltigBis));

		Predicate proGesuchsperiodePredicate = cb.equal(root.get(EbeguVorlage_.proGesuchsperiode), proGesuchsperiode);

		query.where(intervalPredicate, proGesuchsperiodePredicate);
		TypedQuery<EbeguVorlage> q = persistence.getEntityManager().createQuery(query).setParameter(dateParam, date);
		List<EbeguVorlage> resultList = q.getResultList();
		return resultList;
	}

	@Override
	public void copyEbeguVorlageListToNewGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode) {
		// Die Vorlagen des letzten Jahres suchen (datumAb -1 Tag)
		Collection<EbeguVorlage> ebeguVorlageByDate = getALLEbeguVorlageByDate(
			gesuchsperiode.getGueltigkeit().getGueltigAb().minusDays(1), true);
		ebeguVorlageByDate.addAll(getEmptyVorlagen(ebeguVorlageByDate));

		ebeguVorlageByDate.stream().filter(lastYearVoralge -> lastYearVoralge.getName().isProGesuchsperiode()).forEach(lastYearVorlage -> {
			EbeguVorlage newVorlage = lastYearVorlage.copy(gesuchsperiode.getGueltigkeit());
			if (lastYearVorlage.getVorlage() != null) {
				fileSaverService.copy(lastYearVorlage.getVorlage(), "vorlagen");
				newVorlage.setVorlage(lastYearVorlage.getVorlage().copy());
			}
			saveEbeguVorlage(newVorlage);
		});
	}

	@Override
	public Vorlage getBenutzerhandbuch() {
		UserRole userRole = principalBean.discoverMostPrivilegedRole();
		EbeguVorlageKey key = EbeguVorlageKey.getBenutzerHandbuchKeyForRole(userRole);

		final Optional<EbeguVorlage> vorlageOptional = getNewestEbeguVorlageByKey(key);
		EbeguVorlage ebeguVorlage = null;
		if (vorlageOptional.isPresent()) {
			ebeguVorlage = vorlageOptional.get();
		} else {
			EbeguVorlage newVorlage = new EbeguVorlage(key, new DateRange());
			ebeguVorlage = saveEbeguVorlage(newVorlage);
		}
		if (ebeguVorlage.getVorlage() != null) {
			return ebeguVorlage.getVorlage();
		} else {
			try {
				Vorlage vorlage = new Vorlage();
				vorlage.setFilesize("10");
				vorlage.setFilename(key.name() + ".pdf");
				// Das Defaultfile lesen und im Filesystem ablegen
				InputStream is = EbeguVorlageServiceBean.class.getResourceAsStream(key.getDefaultVorlagePath());
				byte[] bytes = IOUtils.toByteArray(is);
				String folder = "benutzerhandbuch";
				UploadFileInfo benutzerhandbuch = fileSaverService.save(bytes, vorlage.getFilename(), folder);
				vorlage.setFilepfad(benutzerhandbuch.getPathWithoutFileName() + File.separator + benutzerhandbuch.getActualFilename());
				return vorlage;
			} catch (IOException | MimeTypeParseException e) {
				LOGGER.error("Could not save vorlage!", e);
				throw new EbeguRuntimeException("getBenutzerhandbuch", "Could not create Benutzerhandbuch", e);
			}
		}
	}

	/**
	 * Adds all empty Vorlagen to the list. It will only take into account those
	 * Vorlage that are set to proGesuchsperiode=true
	 */
	private Set<EbeguVorlage> getEmptyVorlagen(Collection<EbeguVorlage> persistedEbeguVorlagen) {
		Set<EbeguVorlage> emptyEbeguVorlagen = new HashSet<>();
		final EbeguVorlageKey[] ebeguVorlageKeys = EbeguVorlageKey.getAllKeysProGesuchsperiode();
		for (EbeguVorlageKey ebeguVorlageKey : ebeguVorlageKeys) {
			boolean exist = false;
			for (EbeguVorlage ebeguVorlage : persistedEbeguVorlagen) {
				if (ebeguVorlage.getName().equals(ebeguVorlageKey)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				emptyEbeguVorlagen.add(new EbeguVorlage(ebeguVorlageKey));
			}
		}
		return emptyEbeguVorlagen;
	}
}
