package ch.dvbern.ebegu.tests.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Set;


/**
 * Utility-Klasse, um Validierungen in Unit-Tests zu prüfen.
 */
public final class ValidationTestHelper {


	private ValidationTestHelper() {
	}

	/**
	 * Stellt sicher dass mindestens eine beiliebige {@link javax.validation.ConstraintViolation} auf dem mittels Parameter
	 * <tt>bean</tt> gegebenen Bean vorhanden ist.
	 * <p>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param bean das zu prüfende Bean
	 * @param <T>  Type des Beans
	 */
	public static <T> void assertViolated(final @Nonnull T bean) {
		assertViolation(null, bean, null, true, "At least one Validation constraint on bean " + bean);
	}

	/**
	 * Stellt sicher dass mindestens eine beiliebige {@link javax.validation.ConstraintViolation} auf dem mittels Parameter
	 * <tt>bean</tt> gegebenen Bean vorhanden ist.
	 * <p>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param bean   das zu prüfende Bean
	 * @param groups Validations-Gruppen
	 * @param <T>    Type des Beans
	 */
	public static <T> void assertViolated(final @Nonnull T bean, final Class<?>... groups) {
		assertViolation(null, bean, null, true, "At least one Validation constraint on bean " + bean, groups);
	}


	/**
	 * Stellt sicher dass keine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter
	 * <tt>bean</tt> gegebenen Bean vorhanden ist.
	 * <p>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param bean das zu prüfende Bean
	 * @param <T>  Type des Beans
	 */
	public static <T> void assertNotViolated(final @Nonnull T bean) {
		assertViolation(null, bean, null, false, "At least one Validation constraint on bean " + bean);
	}


	/**
	 * Stellt sicher dass keine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> vorhanden ist welche über eine Annotation der
	 * Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz         die Klasse der Annotation
	 * @param bean          das zu prüfende Bean
	 * @param propertyPaths das Property. Wenn null, wird nicht geprüft ob keine Violation auf gegebenem Property ist
	 * @param <T>           Type des Beans
	 */
	public static <T> void assertNotViolated(final @Nonnull Class<? extends Annotation> clazz,
											 final @Nonnull T bean,
											 final @Nonnull String... propertyPaths) {
		assertViolation(clazz, bean, false, "Validation constraint found with Annotation {0} on propertyPath {1}", propertyPaths);
	}

	/**
	 * Stellt sicher dass keine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> vorhanden ist welche über eine Annotation der
	 * Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz  die Klasse der Annotation
	 * @param bean   das zu prüfende Bean
	 * @param groups Validierungs-Gruppen
	 * @param <T>    Type des Beans
	 */
	public static <T> void assertNotViolated(final Class<? extends Annotation> clazz, final T bean, final Class<?>... groups) {
		assertViolation(clazz, bean, null, false, "Validation constraint found with Annotation {0} on propertyPath {1}", groups);
	}

	/**
	 * Convenience-Methode für
	 * <pre>
	 *     <code>
	 *         assertNotViolated(clazz, bean, null);
	 *         </code>
	 * </pre>
	 *
	 * @param clazz die Klasse der Annotation
	 * @param bean  das zu prüfende Bean
	 * @param <T>   Type des Beans
	 */
	public static <T> void assertNotViolated(final @Nonnull Class<? extends Annotation> clazz,
											 final @Nonnull T bean) {
		assertViolation(clazz, bean, null, false, "Validation constraint found with Annotation {0} on propertyPath {1}");
	}


	/**
	 * Stellt sicher dass keine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> vorhanden ist.
	 * <p/>
	 * Wenn dies trotzdem der Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param bean   das zu prüfende Bean
	 * @param groups Validierungs-Gruppen
	 * @param <T>    Type des Beans
	 */
	public static <T> void assertNotViolated(final T bean, final Class<?>... groups) {
		assertViolation(null, bean, null, false, "Validation constraint found with Annotation {0} on propertyPath {1}", groups);
	}


	/**
	 * Stellt sicher dass eine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> vorhanden ist welche über eine Annotation der
	 * Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies wieder erwarten nicht Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz         die Klasse der Annotation
	 * @param bean          das zu prüfende Bean
	 * @param propertyPaths das Property. Wenn null, wird nicht geprüft ob die Violation auf dem Property liegt.
	 * @param <T>           Type des Beans
	 */
	public static <T> void assertViolated(final @Nonnull Class<? extends Annotation> clazz,
										  final @Nonnull T bean,
										  final @Nonnull String... propertyPaths) {
		assertViolation(clazz, bean, true, "No validation constraint found with Annotation {0} on property {1}", propertyPaths);
	}

	/**
	 * Stellt sicher dass eine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> vorhanden ist welche über eine Annotation der
	 * Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies wieder erwarten nicht Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz         die Klasse der Annotation
	 * @param bean          das zu prüfende Bean
	 * @param propertyPaths das Property. Wenn null, wird nicht geprüft ob die Violation auf dem Property liegt.
	 * @param groups        die Validierungs-Gruppen.
	 * @param <T>           Type des Beans
	 */
	public static <T> void assertViolated(final @Nonnull Class<? extends Annotation> clazz,
										  final @Nonnull T bean,
										  final @Nonnull String[] propertyPaths,
										  final Class<?>... groups) {
		assertViolation(clazz, bean, true, "No validation constraint found with Annotation {0} on property {1}", propertyPaths, groups);
	}


	/**
	 * Stellt sicher dass eine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean auf dem gegebenem Property <tt>propertyPath</tt> NICHT vorhanden ist welche über eine Annotation der
	 * Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies wieder erwarten Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz         die Klasse der Annotation
	 * @param bean          das zu prüfende Bean
	 * @param propertyPaths das Property. Wenn null, wird nicht geprüft ob die Violation auf dem Property liegt.
	 * @param groups        die Validierungs-Gruppen.
	 * @param <T>           Type des Beans
	 */
	public static <T> void assertNotViolated(final @Nonnull Class<? extends Annotation> clazz,
											 final @Nonnull T bean,
											 final @Nonnull String[] propertyPaths,
											 final Class<?>... groups) {
		assertViolation(clazz, bean, false, "No validation constraint found with Annotation {0} on property {1}", propertyPaths, groups);
	}


	/**
	 * Convenience-Methode für
	 * <pre>
	 *     <code>
	 *         assertViolated(clazz, bean, null);
	 *         </code>
	 * </pre>
	 *
	 * @param clazz die Klasse der Annotation
	 * @param bean  das zu prüfende Bean
	 * @param <T>   Type des Beans
	 * @see #assertViolated(Class, Object, String...)
	 */
	public static <T> void assertViolated(final @Nonnull Class<? extends Annotation> clazz,
										  final @Nonnull T bean) {
		assertViolation(clazz, bean, null, true, "No validation constraint found with Annotation {0} on property {1}");
	}


	/**
	 * Stellt sicher dass eine {@link javax.validation.ConstraintViolation} auf dem mittels Parameter <tt>bean</tt>
	 * gegebenen Bean vorhanden ist welche über eine Annotation der Klasse <tt>clazz</tt> verursacht worden ist.
	 * <p>
	 * Wenn dies wieder erwarten nicht Fall ist wird ein {@link AssertionError} geworfen.
	 *
	 * @param clazz  die Klasse der Annotation
	 * @param bean   das zu prüfende Bean
	 * @param groups Validierungs-Gruppen
	 * @param <T>    Type des Beans
	 */
	public static <T> void assertViolated(final Class<? extends Annotation> clazz, final T bean, final Class<?>... groups) {
		assertViolation(clazz, bean, null, true, "No validation constraint found with Annotation {0} on property {1}", groups);
	}


	private static <T> void assertViolation(@Nullable final Class<? extends Annotation> clazz, final T bean,
											final boolean expectedMatching, final String messageFormat,
											@Nullable final String[] properties, Class<?>... groups) {
		if (properties != null) {
			for (String property : properties) {
				assertViolation(clazz, bean, property, expectedMatching, messageFormat, groups);
			}
		}
	}

	private static <T> void assertViolation(@Nullable final Class<? extends Annotation> clazz, final T bean, @Nullable final String property,
											final boolean expectedMatching, final String messageFormat, Class... groups) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = null;
		if (groups == null || groups.length == 0) {
			violations = validator.validate(bean);
		} else {
			violations = validator.validate(bean, groups);
		}
		ConstraintViolation<T> matchingViolation = null;
		for (ConstraintViolation<T> violation : violations) {
			if ((clazz == null || violation.getConstraintDescriptor().getAnnotation().annotationType().equals(clazz))
				&& (property == null || violation.getPropertyPath().toString().equals(property))) {
				matchingViolation = violation;
				break;
			}
		}
		if (expectedMatching && matchingViolation == null || !expectedMatching && matchingViolation != null) {

			Class<? extends Annotation> annotation = clazz;
			String prop = property;
			String details = "";
			if (matchingViolation != null) {
				annotation = matchingViolation.getConstraintDescriptor().getAnnotation().annotationType();
				prop = matchingViolation.getPropertyPath().toString();
				details = " - " + matchingViolation;
			}
			throw new AssertionError(MessageFormat.format(messageFormat, annotation, prop) + details);
		}
	}


}
