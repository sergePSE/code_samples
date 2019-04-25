sap.ui.define([
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/graph/ChartController",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/graph/InputController"],
    function (ChartController, InputController) {
        return function (parentControl, graphData, timeDifference) {

            this.drawFragment = function (parentView, graphData) {
                this.panelContainer = sap.ui.xmlfragment(parentView.getId(), "sap.ui.tum.grid.cpu.fragment.RpiChart");
                parentView.addContent(this.panelContainer);
                this.panelContainer.addEventDelegate({"onAfterRendering": () => this.onAfterRendering()}, this);
                this.parentView = parentView;
            };
            // init the component with controller by drawing the initial graph
            this.drawFragment(parentControl, graphData);

            // the second part of init, should be done, when all interface elements are rendered
            this.onAfterRendering = function () {
                if (this.chartController == null)
                {
                    var chartElement = this.panelContainer.getContent()[0].getItems()[0].getDomRef();
                    this.chartController = new ChartController(chartElement, graphData, timeDifference);
                    this.inputController = new InputController(this.panelContainer, graphData, this.chartController,
                        this.timeDifference);
                }
            };

            this.destroy = function() {
                if (this.chartController == null)
                    return;
                this.chartController.destroy();
                //this.parentView.removeContent(this.panelContainer);
                this.panelContainer.destroy();
            };

            this.timeDifference = timeDifference;
        }
});