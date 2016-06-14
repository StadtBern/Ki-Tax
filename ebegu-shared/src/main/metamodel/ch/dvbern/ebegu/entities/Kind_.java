package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Kind.class)
public abstract class Kind_ extends ch.dvbern.ebegu.entities.AbstractPersonEntity_ {

	public static volatile SingularAttribute<Kind, String> bemerkungen;
	public static volatile SingularAttribute<Kind, Integer> wohnhaftImGleichenHaushalt;
	public static volatile SingularAttribute<Kind, PensumFachstelle> pensumFachstelle;
	public static volatile SingularAttribute<Kind, Boolean> unterstuetzungspflicht;
	public static volatile SingularAttribute<Kind, Boolean> familienErgaenzendeBetreuung;
	public static volatile SingularAttribute<Kind, Boolean> mutterspracheDeutsch;

}

