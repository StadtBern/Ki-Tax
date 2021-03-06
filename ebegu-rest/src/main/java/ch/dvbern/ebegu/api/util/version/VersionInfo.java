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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.dvbern.lib.date.converters.LocalDateTimeXMLConverter;

public class VersionInfo implements Serializable {
	private static final long serialVersionUID = -5421524814455690392L;

	private static final Logger LOG = Logger.getLogger(VersionInfo.class.getName());

	@Nullable
	private final String version;
	@Nullable
	private final String artifactGroup;
	@Nullable
	private final String artifactName;
	@Nullable
	@XmlJavaTypeAdapter(LocalDateTimeXMLConverter.class)
	private final LocalDateTime buildTimestamp;
	@Nullable
	private final String builtBy;
	@Nullable
	private final String buildJdk;
	@Nullable
	private final String osName;
	@Nullable
	private final String description;
	@Nullable
	private final String createdBy;

	public VersionInfo(@Nullable String version, @Nullable String artifactGroup, @Nullable String artifactName,
		@Nullable LocalDateTime buildTimestamp, @Nullable String builtBy,
		@Nullable String buildJdk, @Nullable String osName,
		@Nullable String description, @Nullable String createdBy) {
		this.version = version;
		this.buildTimestamp = buildTimestamp;
		this.builtBy = builtBy;
		this.buildJdk = buildJdk;
		this.osName = osName;
		this.artifactGroup = artifactGroup;
		this.artifactName = artifactName;
		this.description = description;
		this.createdBy = createdBy;
	}

	@Nonnull
	public static VersionInfo fromManifest(@Nonnull Manifest mf) {
		Attributes attr = mf.getMainAttributes();

		return new VersionInfo(
			attrValue(attr, "Version"), attrValue(attr, "Artifact-Group"), attrValue(attr, "Built-By"),
			parseBuildDateTime(attr),
			attrValue(attr, "Artifact-Name"), attrValue(attr, "Build-Jdk"),
			attrValue(attr, "OS-Name"),
			attrValue(attr, "Description"),
			attrValue(attr, "Created-By")
		);
	}

	@Nullable
	private static LocalDateTime parseBuildDateTime(@Nonnull Attributes attr) {
		try {
			LocalDateTimeXMLConverter localDateTimeConverter = new LocalDateTimeXMLConverter();
			LocalDateTime buildDateTime = localDateTimeConverter.unmarshal(attrValue(attr, "Build-Timestamp"));
			return buildDateTime;
		} catch (DateTimeParseException ignore) {
			return null;
		}
	}

	@Nullable
	private static String attrValue(@Nonnull Attributes attr, @Nonnull String attrName) {
		try {
			return attr.getValue(attrName);
		} catch (RuntimeException e) {
			LOG.log(Level.INFO, "Could not read attribute value for name: " + attrName, e);
			return null;
		}
	}

	@Nullable
	public String getVersion() {
		return version;
	}

	@Nullable
	public String getArtifactGroup() {
		return artifactGroup;
	}

	@Nullable
	public String getArtifactName() {
		return artifactName;
	}

	@Nullable
	public LocalDateTime getBuildTimestamp() {
		return buildTimestamp;
	}

	@Nullable
	public String getBuiltBy() {
		return builtBy;
	}

	@Nullable
	public String getBuildJdk() {
		return buildJdk;
	}

	@Nullable
	public String getOsName() {
		return osName;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nullable
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public String toString() {
		return "VersionInfo{" +
			"version='" + getVersion() + '\'' +
			", artifactGroup='" + getArtifactGroup() + '\'' +
			", artifactName='" + getArtifactName() + '\'' +
			", buildTimestamp='" + getBuildTimestamp() + '\'' +
			", builtBy='" + getBuiltBy() + '\'' +
			", buildJdk='" + getBuildJdk() + '\'' +
			", osName='" + getOsName() + '\'' +
			", description='" + getDescription() + '\'' +
			", createdBy='" + getCreatedBy() + '\'' +
			'}';
	}
}
