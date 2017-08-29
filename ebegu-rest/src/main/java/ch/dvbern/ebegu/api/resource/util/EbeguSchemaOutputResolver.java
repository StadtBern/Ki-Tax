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
