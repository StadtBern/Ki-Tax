package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.EnumFamilienstatus;
import ch.dvbern.ebegu.enums.EnumGesuchstellerKardinalitaet;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Familiensituation.class)
public abstract class Familiensituation_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Familiensituation, Boolean> gemeinsameSteuererklaerung;
	public static volatile SingularAttribute<Familiensituation, EnumGesuchstellerKardinalitaet> gesuchstellerKardinalitaet;
	public static volatile SingularAttribute<Familiensituation, EnumFamilienstatus> familienstatus;

}

