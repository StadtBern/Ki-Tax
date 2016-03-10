package ch.dvbern.ebegu.api.validation;

import ch.dvbern.ebegu.api.util.ServerMessageUtil;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.api.validation.Validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	private String translatedMessage;
	private String customMessage;
	private ErrorCodeEnum errorCodeEnum;
	private String stackTrace;
	private List<Serializable> argumentList = new ArrayList<>();

//	public EbeguExceptionReport(EbeguException exception) {
//		if (exception != null) {
//			this.exceptionName = exception.getClass().getSimpleName();
//			this.translatedMessage = exception.getMessage();
//			this.customMessage = exception.getCustomMessage();
//			this.methodName = exception.getMethodName();
//			this.argumentList.addAll(exception.getArgs());
//		}
//	}
//
//	public EbeguExceptionReport(EbeguRuntimeException exception) {
//		if (exception != null) {
//			this.exceptionName = exception.getClass().getSimpleName();
//			this.translatedMessage = exception.getMessage();
//			this.customMessage = exception.getCustomMessage();
//			this.methodName = exception.getMethodName();
//			this.argumentList.addAll(exception.getArgs());
//		}
//	}


	public EbeguExceptionReport(@Nullable String exceptionName, @Nullable ErrorCodeEnum errorCodeEnum, @Nullable String methodName, @Nullable String translatedMessage, @Nullable String customMessage, @Nullable List<Serializable> argumentList) {
		this.exceptionName = exceptionName;
		this.errorCodeEnum = errorCodeEnum;
		this.methodName = methodName;
		this.translatedMessage = translatedMessage;
		this.customMessage = customMessage;
		this.argumentList = argumentList;
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

	public String getTranslatedMessage() {
		return translatedMessage;
	}

	public void setTranslatedMessage(String translatedMessage) {
		this.translatedMessage = translatedMessage;
	}

	public List<Serializable> getArgumentList() {
		return argumentList;
	}


	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public ErrorCodeEnum getErrorCodeEnum() {
		return errorCodeEnum;
	}

	public void setErrorCodeEnum(ErrorCodeEnum errorCodeEnum) {
		this.errorCodeEnum = errorCodeEnum;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public void setArgumentList(List<Serializable> argumentList) {
		this.argumentList = argumentList;
	}

	public static Response buildResponse(Response.Status status, EbeguException ex,  Locale localeFromHeader, boolean addDebugInfo) {
		Response.ResponseBuilder builder = setResponseHeaderAndStatus(status);
		String translatedEnumMessage = ServerMessageUtil.translateEnumValue(ex.getErrorCodeEnum(), localeFromHeader);
		EbeguExceptionReport exceptionReport = new EbeguExceptionReport(ex.getClass().getSimpleName(), ex.getErrorCodeEnum(), ex.getMethodName(), translatedEnumMessage, ex.getCustomMessage(), ex.getArgs());
		if (addDebugInfo) {
					addDevelopmentDebugInformation(exceptionReport, ex);
				}
		return builder.entity(exceptionReport).build();

	}

	public static Response buildResponse(Response.Status status, EbeguRuntimeException ex, Locale localeFromHeader, boolean addDebugInfo) {
		Response.ResponseBuilder builder = setResponseHeaderAndStatus(status);
		String translatedEnumMessage = ServerMessageUtil.translateEnumValue(ex.getErrorCodeEnum(), localeFromHeader);
		EbeguExceptionReport exceptionReport = new EbeguExceptionReport(ex.getClass().getSimpleName(), ex.getErrorCodeEnum(), ex.getMethodName(), translatedEnumMessage, ex.getCustomMessage(), ex.getArgs());
		if (addDebugInfo) {
			addDevelopmentDebugInformation(exceptionReport, ex);
		}
		return builder.entity(exceptionReport).build();

	}

	private static void addDevelopmentDebugInformation(EbeguExceptionReport exceptionReport, Exception e) {
			exceptionReport.setStackTrace(ExceptionUtils.getStackTrace(e));
	}

	@Nonnull
	private static Response.ResponseBuilder setResponseHeaderAndStatus(Response.Status status) {
		Response.ResponseBuilder builder = Response.status(status);
		builder.header(Validation.VALIDATION_HEADER, "true");
		builder.type(MediaType.APPLICATION_JSON_TYPE);
		return builder;
	}


}
