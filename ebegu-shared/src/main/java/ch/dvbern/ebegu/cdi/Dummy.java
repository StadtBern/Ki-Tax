package ch.dvbern.ebegu.cdi;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier Annotation fuer CDI welche verwendet werden kann um die dummy Version eines services zu markieren und um beim Injection
 * Point explizit die Dummy Version eiens Services zu verlangen
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Dummy {}
