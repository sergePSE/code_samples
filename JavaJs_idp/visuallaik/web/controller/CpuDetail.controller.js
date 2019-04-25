sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/ui/core/routing/History",
    "sap/ui/tum/grid/cpu/app/cpuDetail/NodeMonitor",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/PageContextContainer"
], function (Controller, JSONModel, History, NodeMonitor, PageContextContainer) {
    "use strict";
    // controller for the rpi details page
    return Controller.extend("sap.ui.tum.grid.cpu.controller.CpuDetail", {

        onInit: function () {
            // add routing feature and read rpi id from the url path
            var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            // add json model, which can be dynamically edited
            this.oModel = new JSONModel({});
            // delegate node information depicting to the monitor

            this.getView().setModel(this.oModel);
            oRouter.getRoute("detail").attachPatternMatched(this._onObjectMatched, this);
        },

        onNodeContextReceived : function(nodeData) {
            if (this.rootContext != null)
                this.rootContext.destroy();
            this.rootContext = new PageContextContainer({
                "performance": this.getView().byId("performanceTab"),
                "overview": this.getView().byId("overviewTab"),
                "task": this.getView().byId("taskTab")
            }, nodeData);
        },

        _onObjectMatched: function (oEvent) {
            if (this.nodeMonitor != null)
                this.nodeMonitor.destroy();
            this.nodeMonitor = new NodeMonitor(this.oModel, this.onNodeContextReceived.bind(this));
            this.nodeMonitor.setDefaultJsonModel();
            this.rpiId = oEvent.getParameter("arguments").index;
            if (this.rootContext != null)
                this.rootContext.destroy();
            this.nodeMonitor.onNodeIdLoad(this.rpiId);

        },
        onNavBack: function () {
            // return back to te previous page
            sap.ui.getCore().getEventBus().publish("DetailPage", "collapseHeader");
            var oHistory = History.getInstance();
            var sPreviousHash = oHistory.getPreviousHash();

            if (sPreviousHash !== undefined) {
                window.history.go(-1);
            } else {
                var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
                oRouter.navTo("overview", {}, true);
            }
        },

        // button events
        turnOn:function () {
            this.nodeMonitor.turnOnCommand();
        },
        turnOff:function () {
            this.nodeMonitor.turnOffCommand();
        },
        reboot:function () {
            this.nodeMonitor.rebootCommand();
        },
        // onKillTaskButtonPress: function () {
        //     if (this.taskController)
        //         this.taskController.onKillButtonPress();
        // }
    });
});