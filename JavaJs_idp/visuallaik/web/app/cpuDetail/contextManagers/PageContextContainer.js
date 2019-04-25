sap.ui.define(["sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/TabContextContainer",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/NodeContextLoader"],
    function (TabContextContainer, NodeContextLoader) {
    return function (tabNameRootElementMap, nodeData) {
        this.destroy = function(){
            this.dataLoader.destroy();
            Object.values(this.tabControllers).forEach((tabController) => {tabController.destroy();});
            this.tabControllers = {};
        };

        this.onDataChange = function(changedContext) {

        };

        function init() {
            this.dataLoader = new NodeContextLoader(window.location.origin, onNodeContextReceived.bind(this));
            this.tabControllers = {};
            Object.keys(tabNameRootElementMap).forEach((tabName) => {
                this.tabControllers[tabName] = new TabContextContainer(tabNameRootElementMap[tabName],
                    this.timeDifference);
            });
            this.dataLoader.loadData(this.nodeId);
        };

        function onNodeContextReceived(context) {
            Object.keys(this.tabControllers).forEach((tabName)=> {
               var tabData = {
                   graphContexts: context.graphContexts.filter(context => context.placement == tabName),
                   activeTasks: context.activeTasks.filter(context => context.placement == tabName),
                   customDataContexts: context.customDataContexts.filter(context => context.placement == tabName)
               };
               this.tabControllers[tabName].init(tabData, this.timeDifference);
            });
        };
        this.nodeId = nodeData.id;
        this.timeDifference = nodeData.timeDifference;
        init.bind(this)();
    };
});