package ch.dvbern.ebegu.api.util.version;/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

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

import ch.dvbern.ebegu.converters.LocalDateTimeXMLConverter;

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
