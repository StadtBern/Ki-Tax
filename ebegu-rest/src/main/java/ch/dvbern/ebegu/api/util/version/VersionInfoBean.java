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
import java.io.InputStream;
import java.util.Optional;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VersionInfoBean {

	private static final Logger LOG = LoggerFactory.getLogger(VersionInfoBean.class);

	@Inject
	private ServletContext context;

	@Nullable
	private VersionInfo versionInfo = null;

	@PostConstruct
	public void postConstruct() {
		versionInfo = readVersionInfo();
	}

	@Nullable
	private VersionInfo readVersionInfo() {
		InputStream is = context.getResourceAsStream("META-INF/MANIFEST.MF");
		if (is == null) {
			LOG.warn("Could not read versionInfo. InputStream is NULL.");
			return null;
		}

		try {
			return VersionInfo.fromManifest(new Manifest(is));
		} catch (IOException e) {
			LOG.warn("Could not read versionInfo", e);
			return null;
		}
	}

	@Nonnull
	public Optional<VersionInfo> getVersionInfo() {
		return Optional.ofNullable(versionInfo);
	}
}
