package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Fall.class)
public abstract class Fall_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Fall, Long> fallNummer;
	public static volatile SingularAttribute<Fall, Integer> nextNumberKind;
	public static volatile SingularAttribute<Fall, Benutzer> verantwortlicher;

}

