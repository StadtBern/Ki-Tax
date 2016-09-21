package ch.dvbern.ebegu.dto.suchfilter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Leider generiert SmartTable  ein verschachteltes JSON Objekt fuer die Suchpredicates. Daher muessen wir das hier nachbauen
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class AntragSearchDTO implements Serializable{

	private static final long serialVersionUID = 4561877549058241575L;
	private PredicateObjectDTO predicateObject;

	public PredicateObjectDTO getPredicateObject() {
		return predicateObject;
	}

	public void setPredicateObject(PredicateObjectDTO predicateObject) {
		this.predicateObject = predicateObject;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("predicateObject", predicateObject)
			.toString();
	}
}
