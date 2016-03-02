package ch.dvbern.ebegu.persistence;

import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.lib.cdipersistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Hilfsklasse welche CriteriaQueries erstellt.
 */
@SuppressWarnings({ "unchecked" })
@Dependent
public class CriteriaQueryHelper {

	@Inject
	private Persistence<AbstractEntity> persistence;


	@SuppressWarnings("unchecked")
	public <T> Collection<T> getAll(final Class<T> clazz) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<T> query = cb.createQuery(clazz);
		query.from(clazz);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	public <T extends P, P extends AbstractEntity> Collection<T> getAllOrdered(@Nonnull final Class<T> clazz, @Nonnull SingularAttribute<P, String> orderBy){
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<T> query = cb.createQuery(clazz);
		Root root = query.from(clazz);
		query.select(root);
		query.orderBy(cb.asc(root.get(orderBy)));
		return persistence.getCriteriaResults(query);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <A, E extends AbstractEntity> E getEntityByUniqueAttribute(@Nonnull final Class<E> entityClazz,
																	  @Nullable final A attributeValue,
																	  @Nonnull final SingularAttribute<E, A> attribute) throws EbeguException {
		final Collection<E> results = getEntitiesByAttribute(entityClazz, attributeValue, attribute);
		if (results.isEmpty()) {
			throw new EbeguEntityNotFoundException(entityClazz, attributeValue.toString(), attribute.getName());
		}
		return ensureSingleResult(results, attributeValue);
	}

	@Nullable
	private <A, E> E ensureSingleResult(@Nonnull final Collection<E> results, @Nullable final A attributeValue) {
		if (results.size() > 1) {
			throw new NonUniqueResultException("Attribute '" + attributeValue +
				"' should be unique, therefore there may not be multiple occurences");
		}
		E retVal = null;
		if (!results.isEmpty()) {
			retVal = results.iterator().next();
		}
		return retVal;
	}

	@Nonnull
	public <A, E> Collection<E> getEntitiesByAttribute(@Nonnull final Class<E> entityClass, @Nullable final A attributeValue, @Nonnull final Attribute<E, A> attribute) {
		final CriteriaBuilder builder = persistence.getCriteriaBuilder();
		final CriteriaQuery<E> query = builder.createQuery(entityClass);
		final Root<E> root = query.from(entityClass);
		final Expression<E> expression;
		if (attribute instanceof SingularAttribute) {
			expression = root.get((SingularAttribute) attribute);
		} else if (attribute instanceof PluralAttribute) {
			expression = root.get((PluralAttribute) attribute);
		} else {
			throw new IllegalArgumentException("attribute must be a PluralAttribute or a SingularAttribute");
		}
		query.where(builder.equal(expression, attributeValue));
		return persistence.getCriteriaResults(query);
	}

	@Nullable
	public static Expression<Boolean> concatenateExpressions(@Nonnull final CriteriaBuilder builder, @Nonnull final List<Expression<Boolean>> predicatesToUse) {
		Expression<Boolean> concatenated = null;
		for (final Expression<Boolean> expression : predicatesToUse) {
			if (concatenated == null) {
				// Dies ist die erste
				concatenated = expression;
			} else {
				// anhaengen
				concatenated = builder.and(concatenated, expression);
			}
		}
		return concatenated;
	}

	@Nullable
	public static Expression<Boolean> concatenateExpressions(@Nonnull final CriteriaBuilder builder, @Nonnull Expression<Boolean>... predicatesToUse) {
		return concatenateExpressions(builder, Arrays.asList(predicatesToUse));
	}
}

