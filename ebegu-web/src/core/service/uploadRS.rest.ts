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
    //uploadService: any;

    static $inject = ['$http', 'REST_API', '$log', 'Upload', 'EbeguRestUtil', '$q'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, $log: ILogService, private upload: any, ebeguRestUtil: EbeguRestUtil, $q: IQService) {
        this.serviceURL = REST_API + 'upload';
        this.http = $http;
        this.log = $log;
        this.q = $q;
        this.ebeguRestUtil = ebeguRestUtil;
        console.log('uploadService', this.upload);
    }

    public uploadFile(files: any, dokumentGrund: TSDokumentGrund, gesuchID: string): IPromise<TSDokumentGrund> {

        var deferred = this.q.defer();

        let restDokumentGrund = {};
        restDokumentGrund = this.ebeguRestUtil.dokumentGrundToRestObject(restDokumentGrund, dokumentGrund);
        let restDokumentString = this.upload.json(restDokumentGrund);

        let names: string [] = [];
        for (let file of files) {
            names.push(file.name);
        }

        this.upload.upload({
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
            console.log('SUCCESS');
            this.log.debug('PARSING traegerschaft REST object ', response.data);

            deferred.resolve(this.ebeguRestUtil.parseDokumentGrund(new TSDokumentGrund(), response.data));
        }, (response: any) => {
            console.log('NOT SUCCESS');
            // anhang.progress = -1;
            // if (hasFileTooLargeError(response)) {
            //     anhang.errorMessage = 'ERR_FILE_TOO_LARGE';
            // } else {
            //     anhang.errorMessage = 'ERR_UPLOAD_FAILED';
            // }
            // deferred.reject(anhang);
        }, (evt: any) => {
            let loaded: number = evt.loaded;
            let total: number = evt.total;
            var progressPercentage: number = 100.0 * loaded / total;
            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
            // anhang.filename = evt.config.data.file.name;
            // anhang.contentType = evt.config.data.file.type;
            // anhang.progress = progressPercentage;
            //
            // deferred.notify(anhang);
        });

        return deferred.promise;

    }

    public getServiceName(): string {
        return 'UploadRS';
    }
}
