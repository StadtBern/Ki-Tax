package ch.dvbern.ebegu.validators;

import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.services.EbeguParameterService;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Validator fuer den Maximalen Zuschlag zum Erwerbspensum
 */
public class CheckZuschlagErwerbspensumMaxZuschlagValidator implements ConstraintValidator<CheckZuschlagErwerbspensumMaxZuschlag, Erwerbspensum> {

	@SuppressWarnings("CdiInjectionPointsInspection")
	@Inject
	private EbeguParameterService ebeguParameterService;

	// We need to pass to EbeguParameterService a new EntityManager to avoid errors like ConcurrentModificatinoException. So we create it here
	// and pass it to the methods of EbeguParameterService we need to call.
	//http://stackoverflow.com/questions/18267269/correct-way-to-do-an-entitymanager-query-during-hibernate-validation
	@PersistenceUnit(unitName = "ebeguPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;


	public CheckZuschlagErwerbspensumMaxZuschlagValidator() {
	}

	/**
	 * Constructor fuer tests damit service reingegeben werden kann
	 * @param service service zum testen
	 */
	public CheckZuschlagErwerbspensumMaxZuschlagValidator(EbeguParameterService service, EntityManagerFactory entityManagerFactory){
		this.ebeguParameterService = service;
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void initialize(CheckZuschlagErwerbspensumMaxZuschlag constraintAnnotation) {
		// nop
	}

	@Override
	public boolean isValid(Erwerbspensum erwerbspensum, ConstraintValidatorContext constraintValidatorContext) {
		if (erwerbspensum.getZuschlagsprozent() == null) {
			return true;
		}
		final EntityManager em = createEntityManager();
		LocalDate stichtagParameter = erwerbspensum.getGueltigkeit().getGueltigAb();
		int maxValue = getMaxValue(stichtagParameter, em);
        closeEntityManager(em);
        return erwerbspensum.getZuschlagsprozent() <= maxValue;
	}

	private EntityManager createEntityManager() {
		if (entityManagerFactory != null) {
			return  entityManagerFactory.createEntityManager(); // creates a new EntityManager
		}
		return null;
	}

	private void closeEntityManager(EntityManager em) {
		if (em != null) {
			em.close();
		}
	}

	/**
	 * Returns the corresponding minimum value for the given betreuungsangebotTyp.
	 *
	 * @param stichtag defines which parameter to load. We only look for params that are valid on this day
	 * @return The minimum value for the betreuungsangebotTyp. Default value is -1: This means if the given betreuungsangebotTyp doesn't match any
	 * recorded type, the min value will be 0 and any positive value will be then accepted
	 */
	private int getMaxValue(LocalDate stichtag, final EntityManager em) {
		EbeguParameterKey key = EbeguParameterKey.PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM;
		Optional<EbeguParameter> parameter = ebeguParameterService.getEbeguParameterByKeyAndDate(key, stichtag, em);
		if (parameter.isPresent()) {
			return parameter.get().getValueAsInteger();
		}
		LoggerFactory.getLogger(this.getClass()).warn("No Value available for Validation of key " + key + ". Using 100");
		return 100; // if no parameter value is stored in database then use 100
	}
}
