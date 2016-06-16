package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Geschlecht;
import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractPersonEntity.class)
public abstract class AbstractPersonEntity_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AbstractPersonEntity, LocalDate> geburtsdatum;
	public static volatile SingularAttribute<AbstractPersonEntity, String> vorname;
	public static volatile SingularAttribute<AbstractPersonEntity, Geschlecht> geschlecht;
	public static volatile SingularAttribute<AbstractPersonEntity, String> nachname;

}

