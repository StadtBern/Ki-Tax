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

package ch.dvbern.ebegu.api.resource.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * This class is needed to export a JAXB annotated class as an xsd schema
 */
public class EbeguSchemaOutputResolver extends SchemaOutputResolver {

	private final StringWriter stringWriter = new StringWriter();

	@Override
	public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
		StreamResult result = new StreamResult(stringWriter);
		result.setSystemId(suggestedFileName);
		return result;
	}

	public String getSchema() {
		return stringWriter.toString();
	}

}
