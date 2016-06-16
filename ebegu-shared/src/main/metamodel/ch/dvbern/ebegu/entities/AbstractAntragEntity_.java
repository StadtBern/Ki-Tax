package ch.dvbern.ebegu.entities;

import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractAntragEntity.class)
public abstract class AbstractAntragEntity_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AbstractAntragEntity, Gesuchsperiode> gesuchsperiode;
	public static volatile SingularAttribute<AbstractAntragEntity, Fall> fall;
	public static volatile SingularAttribute<AbstractAntragEntity, LocalDate> eingangsdatum;

}

