package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AntragStatus;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AntragStatusHistory.class)
public abstract class AntragStatusHistory_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<AntragStatusHistory, LocalDateTime> datum;
	public static volatile SingularAttribute<AntragStatusHistory, Benutzer> benutzer;
	public static volatile SingularAttribute<AntragStatusHistory, Gesuch> gesuch;
	public static volatile SingularAttribute<AntragStatusHistory, AntragStatus> status;

}

