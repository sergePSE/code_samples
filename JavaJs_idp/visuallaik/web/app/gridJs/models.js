var statusColors = {"true": "#008000", "false" :"#000000"};
var defaultColor = "#000000";

sap.ui.define(function () {
    return {
        nodeConstructor: function (rawNodeData) {
            Object.assign(this, rawNodeData);
            this.getPosition = function (nodeWidth) {
                return {
                    x: (rawNodeData.column * 2 + 1) * nodeWidth,
                    y: (rawNodeData.row * 2 + 1) * nodeWidth
                };
            };

            this.getColor = function () {
                if (statusColors.hasOwnProperty(this.online))
                    return statusColors[this.online];
                return defaultColor;
            };
            return this;
        },
        getGridDescription : function(nodes)
        {
            var topology = {
                columnCount: Math.max.apply(null, nodes.map((node) => {return node.column;})) + 1,
                rowCount: Math.max.apply(null, nodes.map((node) => {return node.row;})) + 1
            };
            return topology;
        }
    }
});
