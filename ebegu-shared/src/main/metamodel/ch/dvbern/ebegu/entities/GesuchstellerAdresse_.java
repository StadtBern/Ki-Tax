package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(GesuchstellerAdresse.class)
public abstract class GesuchstellerAdresse_ extends ch.dvbern.ebegu.entities.Adresse_ {

	public static volatile SingularAttribute<GesuchstellerAdresse, Gesuchsteller> gesuchsteller;
	public static volatile SingularAttribute<GesuchstellerAdresse, AdresseTyp> adresseTyp;
	public static volatile SingularAttribute<GesuchstellerAdresse, Boolean> nichtInGemeinde;

}

