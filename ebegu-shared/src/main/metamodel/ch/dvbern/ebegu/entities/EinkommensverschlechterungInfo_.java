package ch.dvbern.ebegu.entities;

import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EinkommensverschlechterungInfo.class)
public abstract class EinkommensverschlechterungInfo_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<EinkommensverschlechterungInfo, LocalDate> stichtagFuerBasisJahrPlus1;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, LocalDate> stichtagFuerBasisJahrPlus2;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Boolean> gemeinsameSteuererklaerung_BjP2;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Boolean> einkommensverschlechterung;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Boolean> ekvFuerBasisJahrPlus2;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Boolean> ekvFuerBasisJahrPlus1;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Gesuch> gesuch;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, Boolean> gemeinsameSteuererklaerung_BjP1;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, String> grundFuerBasisJahrPlus2;
	public static volatile SingularAttribute<EinkommensverschlechterungInfo, String> grundFuerBasisJahrPlus1;

}

