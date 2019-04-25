sap.ui.define([
    "sap/ui/tum/grid/cpu/controller/detailpage_subs/rpi_action/RpiMenuContextGenerator"
], function (contextMenuGenerator) {
    var MenuBuilder = new function () {
        this.selectedNode = null;
        this.buildMenu = function(uiMenuControlOwner, jsonModel)
        {
            // insert html context menu on the page
            var menuFragment = sap.ui.xmlfragment(
                "sap.ui.tum.grid.cpu.fragment.CpuContextMenu",
                uiMenuControlOwner
            );
            var menuModel = contextMenuGenerator.getModel(true);
            jsonModel.setData(menuModel, true);
            uiMenuControlOwner.getView().addDependent(menuFragment);
            // bind submodel to menu
            menuFragment.bindElement("/contextMenu");
            return menuFragment;
        };
        this.openMenu = function(node, d3Object, menuControl, jsonModel)
        {
            this.selectedNode = node;
            var menuModel = contextMenuGenerator.getModel(!node.online);
            jsonModel.setData(menuModel, true);

            var eDock = sap.ui.core.Popup.Dock;
            menuControl.open(false, d3Object, eDock.BeginTop, eDock.BeginBottom, d3Object);
        };
    };
    return MenuBuilder;
});