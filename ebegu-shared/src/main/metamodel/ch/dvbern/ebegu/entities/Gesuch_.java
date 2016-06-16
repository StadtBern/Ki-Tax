package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Gesuch.class)
public abstract class Gesuch_ extends ch.dvbern.ebegu.entities.AbstractAntragEntity_ {

	public static volatile SetAttribute<Gesuch, KindContainer> kindContainers;
	public static volatile SingularAttribute<Gesuch, Familiensituation> familiensituation;
	public static volatile SingularAttribute<Gesuch, Gesuchsteller> gesuchsteller1;
	public static volatile SingularAttribute<Gesuch, Gesuchsteller> gesuchsteller2;
	public static volatile SingularAttribute<Gesuch, Boolean> einkommensverschlechterung;

}

