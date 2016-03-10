/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.api;

import io.swagger.jaxrs.config.BeanConfig;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry-Point of all REST-Services. Used by JAX-RS to List all available Service implementations
 */

/*
 * Since we're not using a jax-rs servlet mapping, we must define an Application class that is annotated with the
 * @ApplicationPath annotation. If you return any empty set for by classes and singletons, your WAR will be scanned
 * for JAX-RS annotation resource and provider classes.
 */
@ApplicationPath(EbeguApplicationV1.API_ROOT_PATH)
/*
 * 20 MB ist der WildFly Default. Falls dieser erhoeht werden muss in standalone.xml im subsysten <subsystem xmlns="urn:jboss:domain:undertow:2.0">
 * der http-listener um ein Attribute max-post-size="ALLOWED_BYTE" ergaenzt werden.
 *
 * Beispiel 50 MB:
 * <http-listener name="default" socket-binding="http" redirect-socket="https" max-post-size="52428800" />
 */
@MultipartConfig(location = "/tmp", maxFileSize = 1024 * 1024 * 20, maxRequestSize = 1024 * 1024 * 20, fileSizeThreshold = 1024 * 1024 * 20)
public class EbeguApplicationV1 extends Application {

	public static final String PATH_SEPARATOR = "/";
	public static final String API_ROOT_PATH = "/api/v1";

	public EbeguApplicationV1() {
		configureSwagger();
	}

	private void configureSwagger() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setTitle("eBEGU REST Interface");
		beanConfig.setVersion("1.0");
		beanConfig.setSchemes(new String[]{"http"}); //later also add https
		beanConfig.setHost("localhost:8002");
		beanConfig.setBasePath("/api/v1");
		beanConfig.setResourcePackage("ch.dvbern.ebegu.api.resource");
		beanConfig.setScan(true);
		beanConfig.setPrettyPrint(true);
	}

	private static final Set<Class<?>> ALL_CLASSES = new HashSet<>(Arrays.asList(new Class<?>[]{
		/* hier koennten die gewuenschten "richtigen" services eingefuegt werden, wenn leer wird gescannt
//		HistorizationResource.class,
//		ApplicationPropertyResource.class,
//		io.swagger.jaxrs.listing.ApiListingResource.class,
//      io.swagger.jaxrs.listing.SwaggerSerializers.class
		 */
	}));

	private static final Set<Object> ALL_SINGLETONS = new HashSet<>(Arrays.asList(
		new Object[]{
		/* hier koennten exceptionmapper eingefuegt werden
			new BenutzerAlreadyExistsExceptionMapper(),
			new ForbiddenExceptionMapper(),
			new UnauthorizedExceptionMapper(),
		*/
		}));

	@Override
	public Set<Class<?>> getClasses() {
		return ALL_CLASSES;
	}

	@Override
	public Set<Object> getSingletons() {
		return ALL_SINGLETONS;
	}
}

