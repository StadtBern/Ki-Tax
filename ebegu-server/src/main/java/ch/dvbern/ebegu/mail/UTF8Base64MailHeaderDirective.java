package ch.dvbern.ebegu.mail;

import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Freemarker-Direktive welche Text nach Base64 konvertiert und "=?UTF-8?B?" voran und "?=" nachstellt.
 */
public class UTF8Base64MailHeaderDirective implements TemplateDirectiveModel {

	private final static String CHARSET = "UTF-8";

	@Override
	public void execute(final Environment env, final Map params, final TemplateModel[] loopVars, final TemplateDirectiveBody body) throws TemplateException, IOException {
		// Check if no parameters were given:
		if (!params.isEmpty()) {
			throw new TemplateModelException(
				"This directive doesn't allow parameters.");
		}
		if (loopVars.length != 0) {
			throw new TemplateModelException(
				"This directive doesn't allow loop variables.");
		}
		// If there is non-empty nested content:
		if (body != null) {
			// Executes the nested body. Same as <#nested> in FTL, except
			// that we use our own writer instead of the current output writer.
			body.render(new Base64Writer(env.getOut()));
		} else {
			throw new EbeguRuntimeException("execute()", "missing body");
		}
	}

	private static class Base64Writer extends Writer {

		private final Writer out;

		public Base64Writer(final Writer out) {
			this.out = out;
		}

		@Override
		public void write(final char[] cbuf, final int off, final int len) throws IOException {
			final char[] toWrite = new char[len];
			System.arraycopy(cbuf, off, toWrite, 0, len);
			final byte[] utf8bytes = new String(toWrite).getBytes(CHARSET);
			final byte[] utf8Base64Bytes = Base64.encodeBase64(utf8bytes);
			out.write("=?" + CHARSET + "?B?");
			out.write(StringUtils.newStringUtf8(utf8Base64Bytes));
			out.write("?=");
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}

		@Override
		public void close() throws IOException {
			out.close();
		}
	}
}
