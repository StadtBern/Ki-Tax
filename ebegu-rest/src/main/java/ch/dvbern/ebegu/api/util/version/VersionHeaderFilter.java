package ch.dvbern.ebegu.api.util.version;/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class VersionHeaderFilter implements ContainerResponseFilter {

	private static final String X_EBEGU_VERSION = "x-ebegu-version";
	private static final String X_EBEGU_BUILD_TIME = "x-ebegu-build-time";

	@Inject
	private VersionInfoBean versionInfoBean;

	@Override
	public void filter(@Nonnull ContainerRequestContext requestContext, @Nonnull ContainerResponseContext responseContext)
		throws IOException {
		versionInfoBean.getVersionInfo().ifPresent(versionInfo -> {
			responseContext.getHeaders().add(X_EBEGU_VERSION, versionInfo.getVersion());
			if (versionInfo.getBuildTimestamp() != null) {
				responseContext.getHeaders().add(X_EBEGU_BUILD_TIME, versionInfo.getBuildTimestamp());
			}
		});
	}
}
