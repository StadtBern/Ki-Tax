package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Gesuchsteller.class)
public abstract class Gesuchsteller_ extends ch.dvbern.ebegu.entities.AbstractPersonEntity_ {

	public static volatile ListAttribute<Gesuchsteller, GesuchstellerAdresse> adressen;
	public static volatile SingularAttribute<Gesuchsteller, FinanzielleSituationContainer> finanzielleSituationContainer;
	public static volatile SingularAttribute<Gesuchsteller, String> mail;
	public static volatile SingularAttribute<Gesuchsteller, String> zpvNumber;
	public static volatile SetAttribute<Gesuchsteller, ErwerbspensumContainer> erwerbspensenContainers;
	public static volatile SingularAttribute<Gesuchsteller, String> telefon;
	public static volatile SingularAttribute<Gesuchsteller, String> mobile;
	public static volatile SingularAttribute<Gesuchsteller, Boolean> diplomatenstatus;
	public static volatile SingularAttribute<Gesuchsteller, EinkommensverschlechterungContainer> einkommensverschlechterungContainer;
	public static volatile SingularAttribute<Gesuchsteller, String> telefonAusland;

}

