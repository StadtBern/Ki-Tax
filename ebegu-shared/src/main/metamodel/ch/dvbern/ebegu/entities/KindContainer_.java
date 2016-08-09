package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(KindContainer.class)
public abstract class KindContainer_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<KindContainer, Kind> kindGS;
	public static volatile SetAttribute<KindContainer, Betreuung> betreuungen;
	public static volatile SingularAttribute<KindContainer, Kind> kindJA;
	public static volatile SingularAttribute<KindContainer, Integer> kindNummer;
	public static volatile SingularAttribute<KindContainer, Integer> nextNumberBetreuung;
	public static volatile SingularAttribute<KindContainer, Gesuch> gesuch;

}

