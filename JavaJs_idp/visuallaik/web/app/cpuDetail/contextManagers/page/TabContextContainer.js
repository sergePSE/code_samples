sap.ui.define(["sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/CustomdataManager",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/GraphManager",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/TaskdataManager",
    "sap/m/VBox"],
    function (CustomDataManager, GraphManager, TaskdataManager, VBox) {
    return function (rootElement, timeDifference) {

        this.destroy = function() {
            if (this.contexts == null)
                return;
            Object.keys(this.contexts).forEach((id) => {this.contexts[id].destroy();});
            this.contexts = {};
            //this.rootElement.removeAllContent();
            this.customDataBox.destroy();
            // normally destroy should work, but in fact it does not. So, content visual is destroyed manually
            //this.rootElement.rerender();
        };

        this.onDataChange = function(changedContext) {

        };

        this.init = function(tabData) {
            this.customDataBox = new VBox();
            this.rootElement.addContent(this.customDataBox);
            this.contexts = {};
            tabData.customDataContexts.forEach(customData => {
                this.contexts[customData.id] = new CustomDataManager(this.customDataBox, customData);
            });
            tabData.activeTasks.forEach(taskContext => {
                this.contexts[taskContext.id] = new TaskdataManager(this.rootElement, taskContext);
            });
            tabData.graphContexts
                    .filter(graphContext => {return graphContext.sampleSet.length > 0})
                    .forEach(graphContext => {
                this.contexts[graphContext.id] = new GraphManager(this.rootElement, graphContext,
                    this.timeDifference);
            });
        };
        this.timeDifference = timeDifference;
        this.rootElement = rootElement;
    };
});