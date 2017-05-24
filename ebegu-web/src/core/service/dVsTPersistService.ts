import {TSSTPersistObject} from '../../models/TSSTPersistObject';

/**
 * This service stores an array of TSSTPersistObject.
 * The namespace cannot be repeated which means that if a new configuration is saved for an
 * existing namespace, this configuration will overwrite the existing one.
 */
export class DVsTPersistService {

    persistedData: TSSTPersistObject[];

    static $inject: any = [];
    /* @ngInject */
    constructor() {
        this.persistedData = [];
    }

    public saveData(namespace: string, data: any): void {
        let existingData: TSSTPersistObject = this.findNamespace(namespace);
        if (existingData) {
            existingData.data = JSON.stringify(data);
        } else {
            this.persistedData.push(new TSSTPersistObject(namespace, JSON.stringify(data)));
        }
    }

    public loadData(namespace: string): any {
        let existingData: TSSTPersistObject = this.findNamespace(namespace);
        if (existingData) {
            return JSON.parse(existingData.data);
        }
        return undefined;
    }

    /**
     * Deletes the given namespace from the list if it exists and returns true.
     * If it doesn't exist it returns false
     */
    public deleteData(namespace: string): boolean {
        for (let i = 0; i < this.persistedData.length; i++) {
            if (this.persistedData[i].namespace === namespace) {
                this.persistedData.splice(i, 1);
                return true;
            }
        }
        return false;
    }

    private findNamespace(namespace: string): TSSTPersistObject {
        for (let i = 0; i < this.persistedData.length; i++) {
            if (this.persistedData[i].namespace === namespace) {
                return this.persistedData[i];
            }
        }
        return undefined;
    }

}
