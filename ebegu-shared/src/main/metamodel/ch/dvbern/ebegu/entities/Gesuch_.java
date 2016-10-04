package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Gesuch.class)
public abstract class Gesuch_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Gesuch, Gesuchsperiode> gesuchsperiode;
	public static volatile SetAttribute<Gesuch, AntragStatusHistory> antragStatusHistories;
	public static volatile SingularAttribute<Gesuch, String> bemerkungen;
	public static volatile SetAttribute<Gesuch, KindContainer> kindContainers;
	public static volatile SingularAttribute<Gesuch, Fall> fall;
	public static volatile SingularAttribute<Gesuch, LocalDate> eingangsdatum;
	public static volatile SingularAttribute<Gesuch, Familiensituation> familiensituation;
	public static volatile SingularAttribute<Gesuch, Gesuchsteller> gesuchsteller1;
	public static volatile SingularAttribute<Gesuch, AntragTyp> typ;
	public static volatile SingularAttribute<Gesuch, Gesuchsteller> gesuchsteller2;
	public static volatile SingularAttribute<Gesuch, AntragStatus> status;
	public static volatile SingularAttribute<Gesuch, EinkommensverschlechterungInfo> einkommensverschlechterungInfo;

}

