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

package ch.dvbern.ebegu.api.util.version;

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
