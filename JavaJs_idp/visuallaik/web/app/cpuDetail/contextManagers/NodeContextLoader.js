sap.ui.define([], function () {
    return function(baseUrl, onDataReceived) {
        this.loadData = function(id) {
            jQuery.get(baseUrl + "/rest/nodeData", {id: id}, function(stringData) {
                var jsonData = JSON.parse(stringData);
                jsonData.graphContexts.sort((con1, con2) => {return con1.id - con2.id;});
                jsonData.graphContexts.forEach(graphContext => {
                    graphContext.sampleSet.sort((set1, set2) => {return set1.id - set2.id;})
                });

                jsonData.customDataContexts.sort((con1, con2) => {return con1.id - con2.id;});
                onDataReceived(jsonData);
            });
        };

        this.destroy = function () {

        };
    }
})
