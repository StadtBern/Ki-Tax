package ch.dvbern.ebegu.entities;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AuthorisierterBenutzer.class)
public abstract class AuthorisierterBenutzer_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AuthorisierterBenutzer, LocalDateTime> lastLogin;
	public static volatile SingularAttribute<AuthorisierterBenutzer, String> password;
	public static volatile SingularAttribute<AuthorisierterBenutzer, LocalDateTime> firstLogin;
	public static volatile SingularAttribute<AuthorisierterBenutzer, String> authToken;
	public static volatile SingularAttribute<AuthorisierterBenutzer, String> username;

}

