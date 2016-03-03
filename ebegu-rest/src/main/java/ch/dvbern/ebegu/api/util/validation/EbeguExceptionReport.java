package ch.dvbern.ebegu.api.util.validation;

import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import org.jboss.resteasy.api.validation.Validation;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imanol on 02.03.16.
 * Dies ist die Reportklasse fuer {@link EbeguException} und {@link EbeguRuntimeException}
 */
@XmlRootElement(
	name = "ebeguReport"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class EbeguExceptionReport {


	private String exceptionName;
	private String methodName;
	private String message;
	private List<String> argumentList = new ArrayList<>();

	public EbeguExceptionReport(EbeguException exception) {
		if (exception != null) {
			this.exceptionName = exception.getClass().getSimpleName();
			this.message = exception.getMessage();
			this.methodName = exception.getMethodName();
			this.argumentList.addAll(exception.getArgs());
		}

	}

	public EbeguExceptionReport(EbeguRuntimeException exception) {
		if (exception != null) {
			this.exceptionName = exception.getClass().getSimpleName();
			this.message = exception.getMessage();
			this.methodName = exception.getMethodName();
			this.argumentList.addAll(exception.getArgs());
		}

	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getArgumentList() {
		return argumentList;
	}

	public void setArgumentList(List<String> argumentList) {
		this.argumentList = argumentList;
	}

	public static Response buildResponse(Response.Status status, EbeguException ex) {
		Response.ResponseBuilder builder = setResponseHeaderAndStatus(status);
		builder.entity(new EbeguExceptionReport(ex));
		return builder.build();

	}

	public static Response buildResponse(Response.Status status, EbeguRuntimeException ex) {
		Response.ResponseBuilder builder = setResponseHeaderAndStatus(status);
			builder.entity(new EbeguExceptionReport(ex));
			return builder.build();

		}

	@Nonnull
	private static Response.ResponseBuilder setResponseHeaderAndStatus(Response.Status status) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");
		builder.type(MediaType.APPLICATION_JSON_TYPE);
		return builder;
	}


}
