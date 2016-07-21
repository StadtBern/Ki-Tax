package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Land;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Adresse.class)
public abstract class Adresse_ extends ch.dvbern.ebegu.entities.AbstractDateRangedEntity_ {

	public static volatile SingularAttribute<Adresse, String> ort;
	public static volatile SingularAttribute<Adresse, String> gemeinde;
	public static volatile SingularAttribute<Adresse, String> strasse;
	public static volatile SingularAttribute<Adresse, String> hausnummer;
	public static volatile SingularAttribute<Adresse, Land> land;
	public static volatile SingularAttribute<Adresse, String> zusatzzeile;
	public static volatile SingularAttribute<Adresse, String> plz;

}

