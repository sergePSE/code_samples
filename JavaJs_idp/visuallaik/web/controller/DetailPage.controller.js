sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/tum/grid/cpu/app/rpi-grid",
    "sap/ui/model/json/JSONModel",
    "sap/ui/tum/grid/cpu/controller/detailpage_subs/MenuBuilder",
    "sap/ui/tum/grid/cpu/app/NodePowerPoster"
], function (Controller, RpiGrid, JSONModel, MenuBuilder, NodePowerPoster) {
    "use strict";
    // controller of the main page below the panel
    return Controller.extend("sap.ui.tum.grid.cpu.controller.DetailPage", {
        isMenuOpened: false,
        onInit: function () {
            // set an initial view model
            this.oModel = new JSONModel({tooltip: {ip: "no ip"}, isHeaderVisible : true});
            this.detailPage = this.byId("detail");
            this.getView().setModel(this.oModel);

            // if data changed, that update data
            sap.ui.getCore().getEventBus().subscribe("DetailPage", "dataChanged", this.updateData, this);
            sap.ui.getCore().getEventBus().subscribe("AppController", "dataChanged", this.updateData, this);
            // hide the header of the page on navigation
            sap.ui.getCore().getEventBus().subscribe("DetailPage", "collapseHeader", this.onCollapseHeader, this);
            // subscribe on resize to resize the grid
            sap.ui.core.ResizeHandler.register(this.detailPage, jQuery.proxy(this.onResize, this));
            this.nodePowerPoster = new NodePowerPoster(window.location.origin);

        },

        onCollapseHeader: function () {
            this.oModel.setData({isHeaderVisible : !this.oModel.getProperty("/isHeaderVisible")});
        },

        onAfterRendering : function () {
            // create a rpi grid controller, based on d3, that renders a grid
            this.rpiGrid = new RpiGrid(this.onElementHover, this.onElementHoverOut, this.onNodeLeftClick,
                this.onElementMenu, this);
            // check if the parent controller already downloaded a grid data, otherwise wait for subscription
            sap.ui.getCore().getEventBus().publish("AppController", "dataRequest");
        },
        onElementHover: function ({ip, d3Object}) {
            // show tooltip if hover over the rpi element
            this.oModel.setData({tooltip:{ip: ip}}, true);

            // create if not exist
            if (!this._oPopover) {
                // create a tooptip markup on the page and bind to the viewmodel
                this._oPopover = sap.ui.xmlfragment("sap.ui.tum.grid.cpu.fragment.TooltipPopover", this);
                this.getView().addDependent(this._oPopover);
                this._oPopover.bindElement("/tooltip");
            }
            var menuDomRef = this.menuControl == null ? this.menuControl : this.menuControl.getDomRef();
            if (menuDomRef == null)
                this._oPopover.openBy(d3Object);
        },

        detachToolTip: function()
        {
            if (!this._oPopover) {
                return;
            }
            this._oPopover.close();
            this.getView().removeDependent(this._oPopover);
            this._oPopover.destroy();
            this._oPopover = null;
        },

        onElementHoverOut: function () {
            if (this._oPopover)
                this._oPopover.close();
        },

        onElementMenu: function (node, d3Object) {
            if (this.menuControl == null) {
                // create menu on right mouse click
                this.menuControl = MenuBuilder.buildMenu(this, this.oModel);
            }
            MenuBuilder.openMenu(node, d3Object, this.menuControl, this.oModel);
            this.isMenuOpened = true;
        },

        onNodeLeftClick: function(node, d3Object) {
            this.routeToNode(node.id);
        },

        updateData: function (busName, eventName, data) {
            var size = {
                height: jQuery(this.detailPage.getDomRef()).height(),
                width: jQuery(this.detailPage.getDomRef()).width()
            };
            if (this.rpiGrid != null)
                this.rpiGrid.loadData(data, size);
        },

        onResize: function (event) {
            if (this.rpiGrid != undefined)
                this.rpiGrid.onResize(event.size);
        },

        onMenuItemShowStatistics: function () {
            this.routeToNode(MenuBuilder.selectedNode.id);
        },
        routeToNode: function(nodeId) {
            this.detachToolTip();
            if (this.menuControl != null)
                this.menuControl.close();

            var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            oRouter.navTo("detail", {index: nodeId});
        },

        onMenuItemTurnOn: function () {
            if (MenuBuilder.selectedNode != null) {
                this.nodePowerPoster.sendTurnOn(MenuBuilder.selectedNode.id);
            }
        },
        onMenuItemTurnOff: function () {
            if (MenuBuilder.selectedNode != null) {
                this.nodePowerPoster.sendTurnOff(MenuBuilder.selectedNode.id);
            }
        },
        onMenuItemReboot: function () {
            if (MenuBuilder.selectedNode != null) {
                this.nodePowerPoster.sendReboot(MenuBuilder.selectedNode.id);
            }
        }
    });
});