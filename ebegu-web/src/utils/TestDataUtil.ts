import TSErwerbspensumContainer from '../models/TSErwerbspensumContainer';
import TSErwerbspensum from '../models/TSErwerbspensum';
import {TSTaetigkeit} from '../models/enums/TSTaetigkeit';
import DateUtil from './DateUtil';
import {TSDateRange} from '../models/types/TSDateRange';
import {TSZuschlagsgrund} from '../models/enums/TSZuschlagsgrund';
import TSAbstractEntity from '../models/TSAbstractEntity';
import {TSAbstractDateRangedEntity} from '../models/TSAbstractDateRangedEntity';
import Moment = moment.Moment;

export default class TestDataUtil {


    public static setAbstractFieldsUndefined(abstractEntity: TSAbstractEntity) {
        abstractEntity.id = undefined;
        abstractEntity.timestampErstellt = undefined;
        abstractEntity.timestampMutiert = undefined;
    }

    /**
     * @param {string} localDateTimeString string with format YYYY-MM-DDTHH:mm:ss.SSS
     * @returns {?Moment}
     */
    public static createErwerbspensumContainer(): TSErwerbspensumContainer {

        let dummyErwerbspensumContainer: TSErwerbspensumContainer = new TSErwerbspensumContainer();
        dummyErwerbspensumContainer.erwerbspensumGS = this.createErwerbspensum();
        dummyErwerbspensumContainer.erwerbspensumJA = this.createErwerbspensum();
        this.setAbstractFieldsUndefined(dummyErwerbspensumContainer);
        return dummyErwerbspensumContainer;
    }

    static createErwerbspensum(): TSErwerbspensum {
        let dummyErwerbspensum = new TSErwerbspensum();
        dummyErwerbspensum.gesundheitlicheEinschraenkungen = false;
        dummyErwerbspensum.taetigkeit = TSTaetigkeit.ANGESTELLT;
        dummyErwerbspensum.pensum = 80;
        dummyErwerbspensum.gueltigkeit = new TSDateRange(DateUtil.today(), DateUtil.today().add(7, 'months'));
        dummyErwerbspensum.zuschlagZuErwerbspensum = true;
        dummyErwerbspensum.zuschlagsprozent = 20;
        dummyErwerbspensum.zuschlagsgrund = TSZuschlagsgrund.FIXE_ARBEITSZEITEN;
        this.setAbstractFieldsUndefined(dummyErwerbspensum);
        return dummyErwerbspensum;
    }

    static checkGueltigkeitAndSetIfSame(first: TSAbstractDateRangedEntity, second: TSAbstractDateRangedEntity) {
        // Dieses hack wird gebraucht weil um 2 Moment zu vergleichen kann man nicht einfach equal() benutzen sondern isSame
        expect(first.gueltigkeit.gueltigAb.isSame(second.gueltigkeit.gueltigAb)).toBe(true);
        expect(first.gueltigkeit.gueltigBis.isSame(second.gueltigkeit.gueltigBis)).toBe(true);
        first.gueltigkeit.gueltigAb = second.gueltigkeit.gueltigAb;
        first.gueltigkeit.gueltigBis = second.gueltigkeit.gueltigBis;
    }
}
