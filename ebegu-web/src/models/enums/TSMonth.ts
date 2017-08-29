export enum TSMonth {
    VORJAHR = <any> 'VORJAHR',
    JANURAY = <any> 'JANUARY',
    FEBRUARY = <any> 'FEBRUARY',
    MARCH = <any> 'MARCH',
    APRIL = <any> 'APRIL',
    MAY = <any> 'MAY',
    JUNE = <any> 'JUNE',
    JULY = <any> 'JULY',
    AUGUST = <any> 'AUGUST',
    SEPTEMBER = <any> 'SEPTEMBER',
    OCTOBER = <any> 'OCTOBER',
    NOVEMBER = <any> 'NOVEMBER',
    DECEMBER = <any> 'DECEMBER',
}

export function getTSMonthValues(): Array<TSMonth> {
    return [
        TSMonth.JANURAY,
        TSMonth.FEBRUARY,
        TSMonth.MARCH,
        TSMonth.APRIL,
        TSMonth.MAY,
        TSMonth.JUNE,
        TSMonth.JULY,
        TSMonth.AUGUST,
        TSMonth.SEPTEMBER,
        TSMonth.OCTOBER,
        TSMonth.NOVEMBER,
        TSMonth.DECEMBER
    ];
}

export function getTSMonthWithVorjahrValues(): Array<TSMonth> {
    return [
        TSMonth.VORJAHR,
        TSMonth.JANURAY,
        TSMonth.FEBRUARY,
        TSMonth.MARCH,
        TSMonth.APRIL,
        TSMonth.MAY,
        TSMonth.JUNE,
        TSMonth.JULY,
        TSMonth.AUGUST,
        TSMonth.SEPTEMBER,
        TSMonth.OCTOBER,
        TSMonth.NOVEMBER,
        TSMonth.DECEMBER
    ];
}


