package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.UserRole;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Benutzer.class)
public abstract class Benutzer_ extends ch.dvbern.ebegu.entities.AbstractMandantEntity_ {

	public static volatile SingularAttribute<Benutzer, Institution> institution;
	public static volatile SingularAttribute<Benutzer, UserRole> role;
	public static volatile SingularAttribute<Benutzer, String> vorname;
	public static volatile SingularAttribute<Benutzer, String> nachname;
	public static volatile SingularAttribute<Benutzer, Traegerschaft> traegerschaft;
	public static volatile SingularAttribute<Benutzer, String> email;
	public static volatile SingularAttribute<Benutzer, String> username;

}

