import {IHttpService, ILogService, IQService} from 'angular';
import TSDokumentGrund from '../../models/TSDokumentGrund';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IPromise = angular.IPromise;

export class UploadRS {
    serviceURL: string;
    http: IHttpService;
    log: ILogService;
    ebeguRestUtil: EbeguRestUtil;
    q: IQService;

    static $inject = ['$http', 'REST_API', '$log', 'Upload', 'EbeguRestUtil', '$q', 'base64'];

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, $log: ILogService, private upload: any, ebeguRestUtil: EbeguRestUtil,
                $q: IQService, private base64: any) {
        this.serviceURL = REST_API + 'upload';
        this.http = $http;
        this.log = $log;
        this.q = $q;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public uploadFile(files: any, dokumentGrund: TSDokumentGrund, gesuchID: string): IPromise<TSDokumentGrund> {

        let restDokumentGrund = {};
        restDokumentGrund = this.ebeguRestUtil.dokumentGrundToRestObject(restDokumentGrund, dokumentGrund);
        let restDokumentString = this.upload.json(restDokumentGrund);

        let names: string [] = [];
        for (let file of files) {
            if (file) {
                let encodedFilename = this.base64.encode(file.name);
                names.push(encodedFilename);
            }
        }

        return this.upload.upload({
            url: this.serviceURL,
            method: 'POST',
            headers: {
                'x-filename': names.join(';'),
                'x-gesuchID': gesuchID
            },
            data: {
                file: files,
                dokumentGrund: restDokumentString
            }
        }).then((response: any) => {
            return this.ebeguRestUtil.parseDokumentGrund(new TSDokumentGrund(), response.data);
        }, (response: any) => {
            console.log('Upload File: NOT SUCCESS');
            return this.q.reject();
        }, (evt: any) => {
            let loaded: number = evt.loaded;
            let total: number = evt.total;
            let progressPercentage: number = 100.0 * loaded / total;
            console.log('progress: ' + progressPercentage + '% ');
            return this.q.defer().notify();
        });
    }

    public getServiceName(): string {
        return 'UploadRS';
    }
}
