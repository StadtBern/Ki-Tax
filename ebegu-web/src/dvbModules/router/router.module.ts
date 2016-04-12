import "angular";
import "angular-ui-router";
import RouterHelperProvider from "./route-helper-provider";

export default angular.module('dvbAngular.router', [
    'ui.router'
]).provider('RouterHelper', RouterHelperProvider);
