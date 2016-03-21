#Row direktive

Ich lasse das hier mal drin damit man eine Vorlage hat wie man so eine kompllizierte Direktive angeht. Aktuell haben
wir aber beschlossen rows eher auf folgende Art zu definieren:

         <div class="row top5">
             <label data-translate="PROPERTY_NAME" for="propName" class="col-md-4">
             </label>
             <input id="propName" type="text" name="propertyName" ng-model="vm.applicationProperty.Name"
                    ng-maxlength="vm.length" class="col-md-8" required max="vm.length">
             <dv-error-messages ng-if="form.$submitted" for="form.propertyName.$error"
                                class="error col-md-offset-4 col-md-8"></dv-error-messages>
         </div>


## Template fuer neue Row mit Input Feld

Um den Prozess fuer das einfuegen einer neuen row etwas einfacher zu gestalten habe ich mal ein live-template
für intellij definiert

    <div class="row dvrow top5">
        <label data-translate="TODO_LABEL_TEXT" for="$ID$_ID" class="col-md-4">
        </label>
        <input id="$ID$_ID" type="text" name="$NAME$" ng-model="$END$"
               ng-maxlength="vm.length" class="col-md-8" required max="vm.length">
        <dv-error-messages ng-if="form.$submitted" for="form.$NAME$.$error"
                           class="error col-md-offset-4 col-md-8"></dv-error-messages>
    </div>

dieses kann durch eingeba von "dvr" in einem html dokument mit anschliessendem drücken von ctrl+j getriggered werden
