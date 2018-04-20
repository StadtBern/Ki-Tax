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

import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';

export default class ExportRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'export';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public exportVerfuegungenOfAntrag(gesuchID: string): IPromise<any> {
        return this.http.get(this.serviceURL + '/gesuch/' + encodeURIComponent(gesuchID), {}).then((response: any) => {
            this.$log.debug('PARSING fall REST object ', response.data);
            return response.data;
        });
    }

    public getJsonSchemaString(): IPromise<any> {
        return this.http.get(this.serviceURL + '/meta/jsonschema').then((response: any) => {
            return JSON.stringify(response.data, undefined, 2);  //prettyprint
        });
    }

    public getXmlSchemaString(): IPromise<any> {
        return this.http.get(this.serviceURL + '/meta/xsd').then((response: any) => {
            return response.data;
        });
    }

}
