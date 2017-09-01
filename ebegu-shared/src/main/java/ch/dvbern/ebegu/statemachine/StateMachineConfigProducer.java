package ch.dvbern.ebegu.statemachine;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import ch.dvbern.ebegu.enums.AntragEvents;
import ch.dvbern.ebegu.enums.AntragStatus;
import com.github.oxo42.stateless4j.StateMachineConfig;

/**
 * CDI Producer fuer StateMachineConfig die wir in SERf benoetigen
 * <p>
 * Hier werden zudem saemtliche onEntry Actions getriggered und an die Services weiterdelegiert.
 */
@SuppressWarnings({"ClassNamePrefixedWithPackageName", "PMD.UnusedFormalParameter", "VisibilityModifier"})
@Dependent
public class StateMachineConfigProducer {


	private final StateMachineConfig<AntragStatus, AntragEvents> gesuchFSMConfig = new StateMachineConfig<>();

	@Produces
	public StateMachineConfig<AntragStatus, AntragEvents> createStateMachineConfig() {

		gesuchFSMConfig.configure(AntragStatus.IN_BEARBEITUNG_GS)
			.permit(AntragEvents.FREIGEBEN_SCHULAMT, AntragStatus.NUR_SCHULAMT)
			.permit(AntragEvents.FREIGABEQUITTUNG_ERSTELLEN, AntragStatus.FREIGABEQUITTUNG)
			.permit(AntragEvents.FREIGEBEN, AntragStatus.FREIGEGEBEN);

		gesuchFSMConfig.configure(AntragStatus.FREIGABEQUITTUNG)
			.permit(AntragEvents.FREIGEBEN_SCHULAMT, AntragStatus.NUR_SCHULAMT)
			.permit(AntragEvents.FREIGEBEN, AntragStatus.FREIGEGEBEN);

		gesuchFSMConfig.configure(AntragStatus.FREIGEGEBEN)
			.permit(AntragEvents.ERSTES_OEFFNEN_JA, AntragStatus.IN_BEARBEITUNG_JA);

		gesuchFSMConfig.configure(AntragStatus.IN_BEARBEITUNG_JA)
			.permit(AntragEvents.MAHNEN, AntragStatus.ERSTE_MAHNUNG)
			.permit(AntragEvents.GEPRUEFT, AntragStatus.GEPRUEFT);

		gesuchFSMConfig.configure(AntragStatus.GEPRUEFT)
			.permit(AntragEvents.ZUWEISUNG_SCHULAMT, AntragStatus.NUR_SCHULAMT)
			.permit(AntragEvents.VERFUEGUNG_STARTEN, AntragStatus.VERFUEGEN)
			.permit(AntragEvents.VERFUEGEN_OHNE_ANGEBOT, AntragStatus.KEIN_ANGEBOT);

		gesuchFSMConfig.configure(AntragStatus.VERFUEGEN)
			.permit(AntragEvents.VERFUEGEN, AntragStatus.VERFUEGT);

		gesuchFSMConfig.configure(AntragStatus.VERFUEGT)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG)
			.permit(AntragEvents.PRUEFEN_STV, AntragStatus.PRUEFUNG_STV);

		gesuchFSMConfig.configure(AntragStatus.KEIN_ANGEBOT)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG);

		gesuchFSMConfig.configure(AntragStatus.NUR_SCHULAMT)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG);

		gesuchFSMConfig.configure(AntragStatus.BESCHWERDE_HAENGIG)
			.permit(AntragEvents.ZURUECK_NUR_SCHULAMT, AntragStatus.NUR_SCHULAMT)
			.permit(AntragEvents.ZURUECK_VERFUEGT, AntragStatus.VERFUEGT)
			.permit(AntragEvents.ZURUECK_KEIN_ANGEBOT, AntragStatus.KEIN_ANGEBOT)
			.permit(AntragEvents.ZURUECK_PRUEFUNG_STV, AntragStatus.PRUEFUNG_STV)
			.permit(AntragEvents.ZURUECK_IN_BEARBEITUNG_STV, AntragStatus.IN_BEARBEITUNG_STV)
			.permit(AntragEvents.ZURUECK_GEPRUEFT_STV, AntragStatus.GEPRUEFT_STV);

		gesuchFSMConfig.configure(AntragStatus.PRUEFUNG_STV)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG) //?
			.permit(AntragEvents.ERSTES_OEFFNEN_STV, AntragStatus.IN_BEARBEITUNG_STV);

		gesuchFSMConfig.configure(AntragStatus.IN_BEARBEITUNG_STV)
			.permit(AntragEvents.GEPRUEFT_STV, AntragStatus.GEPRUEFT_STV)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG);

		gesuchFSMConfig.configure(AntragStatus.GEPRUEFT_STV)
			.permit(AntragEvents.BESCHWEREN, AntragStatus.BESCHWERDE_HAENGIG)
			.permit(AntragEvents.PRUEFUNG_ABGESCHLOSSEN, AntragStatus.VERFUEGT);

		gesuchFSMConfig.configure(AntragStatus.ERSTE_MAHNUNG)
			.permit(AntragEvents.MAHNUNG_ABGELAUFEN, AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN)
			.permit(AntragEvents.MAHNLAUF_BEENDEN, AntragStatus.IN_BEARBEITUNG_JA);

		gesuchFSMConfig.configure(AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN)
			.permit(AntragEvents.MAHNEN, AntragStatus.ZWEITE_MAHNUNG)
			.permit(AntragEvents.MAHNLAUF_BEENDEN, AntragStatus.IN_BEARBEITUNG_JA);

		gesuchFSMConfig.configure(AntragStatus.ZWEITE_MAHNUNG)
			.permit(AntragEvents.MAHNUNG_ABGELAUFEN, AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN)
			.permit(AntragEvents.MAHNLAUF_BEENDEN, AntragStatus.IN_BEARBEITUNG_JA);

		gesuchFSMConfig.configure(AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN)
			.permit(AntragEvents.MAHNLAUF_BEENDEN, AntragStatus.IN_BEARBEITUNG_JA);

		return gesuchFSMConfig;

	}


}

