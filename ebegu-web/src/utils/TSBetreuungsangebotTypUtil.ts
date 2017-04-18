import {TSBetreuungsangebotTyp} from '../models/enums/TSBetreuungsangebotTyp';
/**
 * Hier findet man unterschiedliche Hilfsmethoden fuer TSBetreuungsangebotTyp
 */
export class TSBetreuungsangebotTypUtil {


    public static getAllBetreuungsangebotTyp(): Array<string> {
        let result: Array<string> = [];
        for (let prop in TSBetreuungsangebotTyp) {
            if ((isNaN(parseInt(prop)))) {
                result.push(prop);
            }
        }
        return result;
    }

    public static getBetreuungsangebotTypRequiringErwerbspensum(): Array<TSBetreuungsangebotTyp> {
        return [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND];
    }

    public static isRequireErwerbspensum(typ: TSBetreuungsangebotTyp): boolean {
        let types: Array<TSBetreuungsangebotTyp> =  TSBetreuungsangebotTypUtil.getBetreuungsangebotTypRequiringErwerbspensum();
        for (let i: number = 0; i < types.length; i++) {
            let obj: TSBetreuungsangebotTyp = types[i];
            if (typ === obj) {
                return true;
            }
        }
        return false;
    }


}
