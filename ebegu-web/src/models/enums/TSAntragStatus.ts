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
    BESCHWERDE_HAENGIG = <any> 'BESCHWERDE_HAENGIG'
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
        TSAntragStatus.BESCHWERDE_HAENGIG
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
        TSAntragStatus.BESCHWERDE_HAENGIG];
    return validStates.indexOf(status) !== -1;
}

export function isAtLeastFreigegebenOrFreigabequittung(status: TSAntragStatus): boolean {
    return isAtLeastFreigegeben(status) || status === TSAntragStatus.FREIGABEQUITTUNG;
}

export function isAnyStatusOfVerfuegt(status: TSAntragStatus): boolean {
    return status === TSAntragStatus.NUR_SCHULAMT || status === TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN ||
        status === TSAntragStatus.VERFUEGT || status === TSAntragStatus.BESCHWERDE_HAENGIG;
}

/**
 * Returns true when the status of the Gesuch is VERFUEGEN or VERFUEGT or NUR_SCHULAMT
 * @returns {boolean}
 */
export function isStatusVerfuegenVerfuegt(status: TSAntragStatus): boolean {
    return isAnyStatusOfVerfuegt(status) || status === TSAntragStatus.VERFUEGEN;
}
