sap.ui.define([
    "sap/ui/tum/grid/cpu/controller/detailpage_subs/rpi_action/RpiPowerMenuContextGenerator"
], function (RpiPowerJsonGenerator) {
    var contextBuilder = RpiPowerJsonGenerator;
    var contextMenuFunc = (obj, isRpiOff) => {obj.menu = true;};
    contextBuilder.subscribe((contextMenuFunc));
    return contextBuilder;
});