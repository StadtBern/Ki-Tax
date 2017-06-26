import {EbeguWebCore} from '../core/core.module';
import {zahlungRun} from './zahlung.route';
import {ZahlungViewComponentConfig} from './component/zahlungView';

export const EbeguWebZahlung =
    angular.module('ebeguWeb.zahlung', [EbeguWebCore.name])
        .run(zahlungRun)
        .component('zahlungView', new ZahlungViewComponentConfig());
