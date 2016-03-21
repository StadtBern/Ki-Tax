/*
 * Copyright (c) 2014 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 *
 * $Id$
 */
package ch.dvbern.ebegu.converters;

import ch.dvbern.ebegu.enums.Land;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.persistence.Converter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Converter(autoApply = true)
@XmlJavaTypeAdapter(value = LandConverter.class, type = Land.class)
public class LandConverter extends XmlAdapter<String, Land>{

	@Nullable
	@Override
	public Land unmarshal(String v) {
		return StringUtils.isEmpty(v) ? null : Land.fromString(v);
	}



	@Nullable
	@Override
	public String marshal(Land v) {
		return v == null ? null : v.name();
	}

}
