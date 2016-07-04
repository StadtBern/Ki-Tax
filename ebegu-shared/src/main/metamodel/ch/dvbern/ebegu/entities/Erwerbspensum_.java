package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Taetigkeit;
import ch.dvbern.ebegu.enums.Zuschlagsgrund;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Erwerbspensum.class)
public abstract class Erwerbspensum_ extends ch.dvbern.ebegu.entities.AbstractPensumEntity_ {

	public static volatile SingularAttribute<Erwerbspensum, Boolean> gesundheitlicheEinschraenkungen;
	public static volatile SingularAttribute<Erwerbspensum, Boolean> zuschlagZuErwerbspensum;
	public static volatile SingularAttribute<Erwerbspensum, Taetigkeit> taetigkeit;
	public static volatile SingularAttribute<Erwerbspensum, Integer> zuschlagsprozent;
	public static volatile SingularAttribute<Erwerbspensum, Zuschlagsgrund> zuschlagsgrund;

}

