package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Institution.class)
public abstract class Institution_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Institution, Mandant> mandant;
	public static volatile SingularAttribute<Institution, String> name;
	public static volatile SingularAttribute<Institution, Boolean> active;
	public static volatile SingularAttribute<Institution, Traegerschaft> traegerschaft;

}

