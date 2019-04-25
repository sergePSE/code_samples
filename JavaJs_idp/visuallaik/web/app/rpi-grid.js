// for rounding rectangles
var roundPercentage = 0.2;
var borderThickness = 0.05;

sap.ui.define(["sap/ui/tum/grid/cpu/resources/d3/d3"],
    function (d3js) {
    return function(hoverHandler, outHoverHandler, leftClickHandler, rightClickHandler, parentComponent) {
        this.getSvg = function(){
            return d3.select("svg");
        };
        this.calculateSizes = function()
        {
            if (this.data.topology == null)
                return;
            var rectangleColWidth = this.containerSize.width / (this.data.topology.columnCount * 2 + 1);
            var rectangleRowWidth = this.containerSize.height / (this.data.topology.rowCount * 2 + 1);
            var rectangleWidth = Math.min(rectangleRowWidth, rectangleColWidth);
            return {
                rectangleWidth: rectangleWidth,
                svgHeight: (this.data.topology.rowCount * 2 + 1) * rectangleWidth,
                svgWidth: (this.data.topology.columnCount * 2 + 1) * rectangleWidth
            };
        };

        this.draw = function()
        {
            if (this.data == null || this.containerSize == null)
                return;
            if (this.data.nodes == null)
                return;
            if (this.data.nodes.length == 0)
                return;

            var svg = this.getSvg();
            var sizes = this.calculateSizes();
            svg.attr("height", this.containerSize.height);
            svg.attr("width", this.containerSize.width);
            svg.selectAll("*").remove();

            var gRects = svg.selectAll("rects")
                .data(this.data.nodes)
                .enter()
                .append("g")
                .on("contextmenu", onRightClickEvent)
                .on("click", onLeftClickEvent)
                .on("mouseover", onNodeHoverEvent)
                .on("mouseout", onNodeMouseOut);

            var rects = gRects.append("rect")
                .attr("x", function (node) { return node.getPosition(sizes.rectangleWidth).x; })
                .attr("y", function (node) { return node.getPosition(sizes.rectangleWidth).y; })
                .attr("id",function(node) { return node.id; })
                .attr("width", function(node) { return sizes.rectangleWidth; })
                .attr("height", function(node) { return sizes.rectangleWidth; })
                .attr("rx", sizes.rectangleWidth * roundPercentage )
                .attr("ry", sizes.rectangleWidth * roundPercentage )
                .style("stroke", function(node) { return node.getColor();})
                .style("stroke-width", sizes.rectangleWidth * borderThickness)
                .style("fill", "#fafafa")
                .attr("class","rectangle")
                .attr("title", function(node) { return `ip:${node.ip}`});

            var texts = gRects.append("text")
                .text(function(node){
                    return node.name;
                })
                .attr("x", function (node) {
                    return sizes.rectangleWidth / 2 + node.getPosition(sizes.rectangleWidth).x ;
                })
                .attr("y", function (node) {
                    return sizes.rectangleWidth / 2 + node.getPosition(sizes.rectangleWidth).y;
                })
                .attr("text-anchor","middle")
                .attr("fill","red");
        };

        function onRightClickEvent(node) {
            d3.event.preventDefault();
            rightClickHandler.bind(parentComponent)(node, d3.event.target);
        };

        function onLeftClickEvent(node) {
            leftClickHandler.bind(parentComponent)(node, d3.event.target);
        };

        function onNodeHoverEvent(node)
        {
            hoverHandler.bind(parentComponent)({ip:node.ip, d3Object: d3.event.target});
        };

        function onNodeMouseOut()
        {
            outHoverHandler.bind(parentComponent)();
        };

        this.loadData = function (data, size) {
            this.data = data;
            if (this.containerSize == null) {
                this.containerSize = size;
            }
            this.draw();
        };

        this.onResize = function(size)
        {
            this.containerSize = size;
            this.draw();
        };

        this.onNodeStatusChange = function({nodeId, nodeStatus}){
            var changedNode = this.data.nodes.find(node => node.id == nodeId);
            if (changedNode == null)
                return;
            changedNode.online = nodeStatus;
            this.getSvg().selectAll("rect").filter(nodeData => {
                if (nodeData.id == nodeId) {
                    nodeData.online == nodeStatus;
                    return true;
                }
                return false;
            }).style("stroke", function(node) { return node.getColor();})
        };

        sap.ui.getCore().getEventBus().subscribe("WebSocket", "onNodeStatusChange",
            (thread, eventId, data) => this.onNodeStatusChange(data), this);
    }
});