/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dvbern.ebegu.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Entry-Point of all REST-Services. Used by JAX-RS to List all available Service implementations
 * Also defines the api root path from the context onwards ebegu/api/v1
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
		beanConfig.setSchemes(new String[] { "http" }); //later also add https
		beanConfig.setHost("localhost:8080");
		beanConfig.setBasePath("/ebegu" + API_ROOT_PATH); //context ist hier auch wichtig
		beanConfig.setResourcePackage("ch.dvbern.ebegu.api.resource");
		beanConfig.setScan(true);
		beanConfig.setPrettyPrint(true);
	}

	private static final Set<Class<?>> ALL_CLASSES = new HashSet<>(Arrays.asList(new Class<?>[] {
		/* hier koennten die gewuenschten "richtigen" services eingefuegt werden, wenn leer wird gescannt
//		HistorizationResource.class,
//		ApplicationPropertyResource.class,
//		io.swagger.jaxrs.listing.ApiListingResource.class,
//      io.swagger.jaxrs.listing.SwaggerSerializers.class
		 */
	}));

	private static final Set<Object> ALL_SINGLETONS = new HashSet<>(Arrays.asList(
		new Object[] {
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

