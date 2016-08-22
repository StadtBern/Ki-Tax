package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.Betreuungsstatus;
import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Betreuung.class)
public abstract class Betreuung_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<Betreuung, InstitutionStammdaten> institutionStammdaten;
	public static volatile SingularAttribute<Betreuung, String> bemerkungen;
	public static volatile SingularAttribute<Betreuung, Boolean> vertrag;
	public static volatile SingularAttribute<Betreuung, LocalDate> datumAblehnung;
	public static volatile SetAttribute<Betreuung, BetreuungspensumContainer> betreuungspensumContainers;
	public static volatile SingularAttribute<Betreuung, Boolean> erweiterteBeduerfnisse;
	public static volatile SingularAttribute<Betreuung, KindContainer> kind;
	public static volatile SingularAttribute<Betreuung, Verfuegung> verfuegung;
	public static volatile SingularAttribute<Betreuung, LocalDate> datumBestaetigung;
	public static volatile SingularAttribute<Betreuung, String> grundAblehnung;
	public static volatile SingularAttribute<Betreuung, Integer> betreuungNummer;
	public static volatile SingularAttribute<Betreuung, Betreuungsstatus> betreuungsstatus;

}

