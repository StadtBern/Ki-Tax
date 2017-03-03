import {EbeguWebCore} from '../core/core.module';
import {zahlungsauftragRun} from './zahlungsauftrag.route';
import {ZahlungsauftragViewComponentConfig} from './component/zahlungsauftragView';

export const EbeguWebZahlungsauftrag =
    angular.module('ebeguWeb.zahlungsauftrag', [EbeguWebCore.name])
        .run(zahlungsauftragRun)
        .component('zahlungsauftragView', new ZahlungsauftragViewComponentConfig());
