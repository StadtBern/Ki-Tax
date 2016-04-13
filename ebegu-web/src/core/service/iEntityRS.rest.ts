import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService} from 'angular';

export interface IEntityRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
}
