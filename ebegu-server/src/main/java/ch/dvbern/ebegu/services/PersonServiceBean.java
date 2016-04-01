package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Person;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Service fuer Person
 */
@Stateless
@Local(PersonService.class)
public class PersonServiceBean extends AbstractBaseService implements PersonService {

	@Inject
	private Persistence<Person> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;


	@Nonnull
	@Override
	public Person createPerson(@Nonnull Person person) {
		Objects.requireNonNull(person);
		return persistence.persist(person);
	}

	@Nonnull
	@Override
	public Person updatePerson(@Nonnull Person person) {
		Objects.requireNonNull(person);
		return persistence.merge(person);//foundPerson.get());
	}

	@Nonnull
	@Override
	public Optional<Person> findPerson(@Nonnull final String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		Person a =  persistence.find(Person.class, id);
		return Optional.ofNullable(a);
	}

	@Override
	@Nonnull
	public Collection<Person> getAllPersonen() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Person.class));
	}

	@Override
	public void removePerson(@Nonnull Person person) {
		Validate.notNull(person);
		Optional<Person> propertyToRemove = findPerson(person.getId());
		propertyToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removePerson", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, person));
		persistence.remove(propertyToRemove.get());
	}
}
