import {TSBetreuungsangebotTyp} from '../models/enums/TSBetreuungsangebotTyp';
/**
 * Hier findet man unterschiedliche Hilfsmethoden fuer TSBetreuungsangebotTyp
 */
export class TSBetreuungsangebotTypUtil {


    public static getAllBetreuungsangebotTyp(): Array<string> {
        let result: Array<string> = [];
        for (var prop in TSBetreuungsangebotTyp) {
            if ((isNaN(parseInt(prop)))) {
                result.push(prop);
            }
        }
        return result;
    }

    public static getBetreuungsangebotTypRequiringErwerbspensum(): Array<TSBetreuungsangebotTyp> {
        return [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND, TSBetreuungsangebotTyp.TAGI];
    }

    public static isRequireErwerbspensum(typ: TSBetreuungsangebotTyp): boolean {
        let types: Array<TSBetreuungsangebotTyp> =  TSBetreuungsangebotTypUtil.getBetreuungsangebotTypRequiringErwerbspensum();
        for (var i: number = 0; i < types.length; i++) {
            var obj: TSBetreuungsangebotTyp = types[i];
            if (typ === obj) {
                return true;
            }
        }
        return false;
    }


}
