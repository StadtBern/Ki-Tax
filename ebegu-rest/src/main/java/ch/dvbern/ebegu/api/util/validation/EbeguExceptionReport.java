package ch.dvbern.ebegu.api.util.validation;

import ch.dvbern.ebegu.errors.EbeguException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by imanol on 02.03.16.
 */
@XmlRootElement(
	name = "ebeguReport"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class EbeguExceptionReport {

	private String exception;
	private String entityClass = new String();
	private String attributeValue = new String();
	private String attribute = new String();

	public EbeguExceptionReport(EbeguException exception) {
		if(exception != null) {
			this.exception = exception.getMessage();
		}

		this.entityClass = exception.getArgs().get(0).toString();
		this.attributeValue = exception.getArgs().get(1).toString();
		this.attribute = exception.getArgs().get(2).toString();
	}

	public String getException() {
		return this.exception;
	}

	public String getEntityClass() {
		return this.entityClass;
	}

	public String getAttributeValue() { return this.attributeValue; }

	public String getAttribute() { return this.attribute;	}


	public void setException(String exception) {
		this.exception = exception;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
