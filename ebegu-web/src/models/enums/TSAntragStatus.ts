export enum TSAntragStatus {
    IN_BEARBEITUNG_GS = <any> 'IN_BEARBEITUNG_GS',
    FREIGABEQUITTUNG = <any> 'FREIGABEQUITTUNG',
    NUR_SCHULAMT = <any> 'NUR_SCHULAMT',
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
    VERFUEGT = <any> 'VERFUEGT'
}

export const IN_BEARBEITUNG_BASE_NAME = 'IN_BEARBEITUNG';

export function getTSAntragStatusValues(): Array<TSAntragStatus> {
    return [
        TSAntragStatus.IN_BEARBEITUNG_GS,
        TSAntragStatus.FREIGABEQUITTUNG,
        TSAntragStatus.NUR_SCHULAMT,
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
        TSAntragStatus.VERFUEGT
    ];
}

/**
 * Gibt alle Werte zurueck ausser VERFUEGT. Diese Werte sind die, die bei der Pendenzenliste notwendig sind
 * @returns {TSAntragStatus[]}
 */
export function getTSAntragStatusPendenzValues(): Array<TSAntragStatus> {
    return getTSAntragStatusValues().filter(element => element !== TSAntragStatus.VERFUEGT);
}

export function isAtLeastFreigegeben(status: TSAntragStatus): boolean {
    let validStates: Array<TSAntragStatus> = [
        TSAntragStatus.NUR_SCHULAMT,
        TSAntragStatus.FREIGEGEBEN,
        TSAntragStatus.NUR_SCHULAMT,
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
        TSAntragStatus.VERFUEGT];
    return validStates.indexOf(status) !== -1;
}
