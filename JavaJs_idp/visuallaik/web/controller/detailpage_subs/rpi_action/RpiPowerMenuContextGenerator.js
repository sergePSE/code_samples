sap.ui.define([
], function () {
    var RpiPowerJsonModelBuilder = new function (){
        var subscribers = [];
        this.subscribe = function(processingFunction) {
            subscribers.push(processingFunction);
        }
        this.getModel = function(isRpiOff)
        {
            var obj = {};
            subscribers.forEach((subscriber) => {subscriber(obj, isRpiOff);});
            return { contextMenu: obj };
        };

        var powerOffFunc = (obj, isRpiOff) => { obj.powerOff = !isRpiOff; };
        var powerOnFunc = (obj, isRpiOff) => { obj.powerOn = isRpiOff; };
        var rebootFunc = (obj, isRpiOff) => { obj.reboot = !isRpiOff; };

        this.subscribe(powerOffFunc);
        this.subscribe(powerOnFunc);
        this.subscribe(rebootFunc);
    };
    return RpiPowerJsonModelBuilder;
});