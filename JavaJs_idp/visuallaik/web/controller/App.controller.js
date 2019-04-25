sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/tum/grid/cpu/app/gridJs/dataLoader",
    "sap/ui/model/json/JSONModel"
], function (Controller, dataLoader, JSONModel) {
    "use strict";
    /* Start controller, that initializes the application
       Also the main one, that loads others
     */
    return Controller.extend("sap.ui.tum.grid.cpu.controller.App", {

        onInit:function() {
            // load a data from the server about rpi nodes
            var dataRequestRelativePath = "/rest/nodes";
            dataLoader.loadData(window.location.origin + dataRequestRelativePath, this.onDataChange.bind(this));
            // activate web socket to receive node actual information change
            dataLoader.subscribeToChanges();
            // create an event bus for child controllers to request a data
            sap.ui.getCore().getEventBus().subscribe("AppController", "dataRequest", this.onDataRequest, this);
            // provide child controllers with nodes info
            sap.ui.getCore().getEventBus().subscribe("AppController", "nodeRequest", this.onNodeRequest, this);

            // set an initial view model
            this.getView().setModel(new JSONModel({isHeaderVisible: false}));

        },
        onDataRequest: function() {
            if (this.data != null)
                sap.ui.getCore().getEventBus().publish("DetailPage", "dataChanged", this.data);
        },

        onAfterRendering: function () {
            if (this.data)
                this.onDataChange(this.data);
        },

        onDataChange: function (data) {
            this.timeDifference = data.timeDifference;
            this.data = data;
            // notify detail page, that rpi nodes changed
            sap.ui.getCore().getEventBus().publish("AppController", "dataChanged", data);
        },

        onNodeRequest: function (thread, eventId) {
            if(this.data == null)
                return;
            this.data.timeDifference = this.timeDifference;
            sap.ui.getCore().getEventBus().publish("CpuDetail", "onNodeResponse", this.data);
        }

    });
});