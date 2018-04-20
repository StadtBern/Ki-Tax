/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {TSSTPersistObject} from '../../models/TSSTPersistObject';
import {IRootScopeService} from 'angular';
import {TSAuthEvent} from '../../models/enums/TSAuthEvent';

/**
 * This service stores an array of TSSTPersistObject.
 * The namespace cannot be repeated which means that if a new configuration is saved for an
 * existing namespace, this configuration will overwrite the existing one.
 */
export class DVsTPersistService {

    persistedData: TSSTPersistObject[];

    static $inject: any = ['$rootScope'];
    /* @ngInject */
    constructor(private $rootScope: IRootScopeService) {
        this.clearAll();
        this.$rootScope.$on(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], () => {
            this.clearAll();
        });
    }

    private clearAll() {
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
