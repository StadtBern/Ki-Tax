export enum TSDokumentGrundTyp {

    FAMILIENSITUATION = <any> 'FAMILIENSITUATION',
    KINDER = <any> 'KINDER',
    ERWERBSPENSUM = <any> 'ERWERBSPENSUM',
    FINANZIELLESITUATION = <any> 'FINANZIELLESITUATION',
    EINKOMMENSVERSCHLECHTERUNG = <any> 'EINKOMMENSVERSCHLECHTERUNG',
    SONSTIGE_NACHWEISE = <any> 'SONSTIGE_NACHWEISE',
    PAPIERGESUCH = <any> 'PAPIERGESUCH'

}

export function getTSTSDokumentTypValues(): Array<TSDokumentGrundTyp> {
    return [
        TSDokumentGrundTyp.FAMILIENSITUATION,
        TSDokumentGrundTyp.KINDER,
        TSDokumentGrundTyp.ERWERBSPENSUM,
        TSDokumentGrundTyp.FINANZIELLESITUATION,
        TSDokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG,
        TSDokumentGrundTyp.SONSTIGE_NACHWEISE,
        TSDokumentGrundTyp.PAPIERGESUCH,
    ];
}
