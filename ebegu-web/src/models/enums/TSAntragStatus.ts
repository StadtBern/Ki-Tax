import {TSRole} from './TSRole';
export enum TSAntragStatus {
    IN_BEARBEITUNG_GS = <any> 'IN_BEARBEITUNG_GS',
    FREIGABEQUITTUNG = <any> 'FREIGABEQUITTUNG',
    NUR_SCHULAMT = <any> 'NUR_SCHULAMT',
    NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN = <any> 'NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN',
    FREIGEGEBEN = <any> 'FREIGEGEBEN',
    ZURUECKGEWIESEN = <any> 'ZURUECKGEWIESEN',
    ERSTE_MAHNUNG = <any> 'ERSTE_MAHNUNG',
    ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN = <any> 'ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN',
    ERSTE_MAHNUNG_ABGELAUFEN = <any> 'ERSTE_MAHNUNG_ABGELAUFEN',
    ZWEITE_MAHNUNG = <any> 'ZWEITE_MAHNUNG',
    ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN = <any> 'ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN',
    ZWEITE_MAHNUNG_ABGELAUFEN = <any> 'ZWEITE_MAHNUNG_ABGELAUFEN',
    IN_BEARBEITUNG_JA = <any> 'IN_BEARBEITUNG_JA',
    GEPRUEFT = <any> 'GEPRUEFT',
    PLATZBESTAETIGUNG_ABGEWIESEN = <any> 'PLATZBESTAETIGUNG_ABGEWIESEN',
    PLATZBESTAETIGUNG_WARTEN = <any> 'PLATZBESTAETIGUNG_WARTEN',
    VERFUEGEN = <any> 'VERFUEGEN',
    VERFUEGT = <any> 'VERFUEGT',
    BESCHWERDE_HAENGIG = <any> 'BESCHWERDE_HAENGIG',
    PRUEFUNG_STV = <any> 'PRUEFUNG_STV',
    IN_BEARBEITUNG_STV = <any> 'IN_BEARBEITUNG_STV',
    GEPRUEFT_STV = <any> 'GEPRUEFT_STV'
}

export const IN_BEARBEITUNG_BASE_NAME = 'IN_BEARBEITUNG';

export function getTSAntragStatusValues(): Array<TSAntragStatus> {
    return [
        TSAntragStatus.IN_BEARBEITUNG_GS,
        TSAntragStatus.FREIGABEQUITTUNG,
        TSAntragStatus.NUR_SCHULAMT,
        TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.FREIGEGEBEN,
        TSAntragStatus.ZURUECKGEWIESEN,
        TSAntragStatus.ERSTE_MAHNUNG,
        TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.ZWEITE_MAHNUNG,
        TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.IN_BEARBEITUNG_JA,
        TSAntragStatus.GEPRUEFT,
        TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN,
        TSAntragStatus.PLATZBESTAETIGUNG_WARTEN,
        TSAntragStatus.VERFUEGEN,
        TSAntragStatus.VERFUEGT,
        TSAntragStatus.BESCHWERDE_HAENGIG,
        TSAntragStatus.PRUEFUNG_STV,
        TSAntragStatus.IN_BEARBEITUNG_STV,
        TSAntragStatus.GEPRUEFT_STV
    ];
}
export function getTSAntragStatusValuesByRole(userrole: TSRole): Array<TSAntragStatus> {
    switch (userrole) {
        case TSRole.STEUERAMT:
            return [
                TSAntragStatus.PRUEFUNG_STV,
                TSAntragStatus.IN_BEARBEITUNG_STV
            ];
        case TSRole.SACHBEARBEITER_JA:
        case TSRole.ADMIN:
        case TSRole.REVISOR:
        case TSRole.JURIST:
            return getTSAntragStatusValues().filter(element => (element !== TSAntragStatus.IN_BEARBEITUNG_GS
                && element !== TSAntragStatus.FREIGABEQUITTUNG && element !== TSAntragStatus.NUR_SCHULAMT
                && element !== TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN));
        case TSRole.SACHBEARBEITER_INSTITUTION:
        case TSRole.SACHBEARBEITER_TRAEGERSCHAFT:
            return getTSAntragStatusValues().filter(element => (element !== TSAntragStatus.PRUEFUNG_STV
            && element !== TSAntragStatus.IN_BEARBEITUNG_STV && element !== TSAntragStatus.GEPRUEFT_STV));
        default:
            return getTSAntragStatusValues();
    }
}

/**
 * Gibt alle Werte zurueck ausser VERFUEGT. Diese Werte sind die, die bei der Pendenzenliste notwendig sind
 * @returns {TSAntragStatus[]}
 */
export function getTSAntragStatusPendenzValues(userrole: TSRole): Array<TSAntragStatus> {
    return getTSAntragStatusValuesByRole(userrole).filter(element => element !== TSAntragStatus.VERFUEGT);
}

export function isAtLeastFreigegeben(status: TSAntragStatus): boolean {
    let validStates: Array<TSAntragStatus> = [
        TSAntragStatus.NUR_SCHULAMT,
        TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.FREIGEGEBEN,
        TSAntragStatus.ZURUECKGEWIESEN,
        TSAntragStatus.ERSTE_MAHNUNG,
        TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.ZWEITE_MAHNUNG,
        TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN,
        TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN,
        TSAntragStatus.IN_BEARBEITUNG_JA,
        TSAntragStatus.GEPRUEFT,
        TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN,
        TSAntragStatus.PLATZBESTAETIGUNG_WARTEN,
        TSAntragStatus.VERFUEGEN,
        TSAntragStatus.VERFUEGT,
        TSAntragStatus.BESCHWERDE_HAENGIG,
        TSAntragStatus.PRUEFUNG_STV,
        TSAntragStatus.IN_BEARBEITUNG_STV,
        TSAntragStatus.GEPRUEFT_STV];
    return validStates.indexOf(status) !== -1;
}

export function isAtLeastFreigegebenOrFreigabequittung(status: TSAntragStatus): boolean {
    return isAtLeastFreigegeben(status) || status === TSAntragStatus.FREIGABEQUITTUNG;
}

export function isAnyStatusOfVerfuegt(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.NUR_SCHULAMT || status === TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN ||
        status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.BESCHWERDE_HAENGIG || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV;
}

export function isAnyStatusOfVerfuegtButSchulamt(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.BESCHWERDE_HAENGIG || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV;
}

export function isVerfuegtOrSTV(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.PRUEFUNG_STV
        || status === TSAntragStatus.IN_BEARBEITUNG_STV || status === TSAntragStatus.GEPRUEFT_STV;
}

/**
 * Returns true when the status of the Gesuch is VERFUEGEN or VERFUEGT or NUR_SCHULAMT
 * @returns {boolean}
 */
export function isStatusVerfuegenVerfuegt(status: TSAntragStatus): boolean {
    return isAnyStatusOfVerfuegt(status) || status === TSAntragStatus.VERFUEGEN;
}
