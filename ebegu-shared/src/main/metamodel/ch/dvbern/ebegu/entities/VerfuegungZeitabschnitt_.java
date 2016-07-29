package ch.dvbern.ebegu.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VerfuegungZeitabschnitt.class)
public abstract class VerfuegungZeitabschnitt_ extends ch.dvbern.ebegu.entities.AbstractDateRangedEntity_ {

	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> fachstellenpensum;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, String> bemerkungen;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, BigDecimal> elternbeitrag;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> anspruchberechtigtesPensum;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> anspruchspensumRest;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, BigDecimal> vollkosten;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> erwerbspensumGS2;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> erwerbspensumGS1;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> bgPensum;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, BigDecimal> massgebendesEinkommen;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> erwerbspensumMinusOffset;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, BigDecimal> betreuungsstunden;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Integer> betreuungspensum;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, Verfuegung> verfuegung;
	public static volatile SingularAttribute<VerfuegungZeitabschnitt, BigDecimal> abzugFamGroesse;

}

