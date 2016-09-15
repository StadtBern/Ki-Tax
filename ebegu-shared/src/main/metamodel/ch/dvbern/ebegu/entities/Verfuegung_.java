package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Verfuegung.class)
public abstract class Verfuegung_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Verfuegung, String> generatedBemerkungen;
	public static volatile ListAttribute<Verfuegung, VerfuegungZeitabschnitt> zeitabschnitte;
	public static volatile SingularAttribute<Verfuegung, Betreuung> betreuung;
	public static volatile SingularAttribute<Verfuegung, String> manuelleBemerkungen;

}

