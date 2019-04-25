sap.ui.define([
    "sap/ui/tum/grid/cpu/resources/moment/moment.min",
    "sap/ui/tum/grid/cpu/resources/chartjs/Chart.min",
    "sap/ui/tum/grid/cpu/resources/chart-plugin-streaming/chartjs-plugin-streaming",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/graph/colors",
    "sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/graph/GraphDataLoader"],
function (momentjs, chartjs, chart_plugin_streaming, colors, GraphDataLoader) {
    return function (chartElement, graphData, timeDifference) {

        // datasetSources contains name, historySource, onlineSource
        function generateDatasets(graphData, graphParams) {
            var datasets = [];
            graphData.sampleSet.forEach(function (datasetSource, i) {
                datasets.push({
                    label: datasetSource.name,
                    borderColor: colors.getBorderColor(i),
                    backgroundColor: colors.getBackgroundColor(i),
                    data: [],
                    fill: false,
                    datasetId: datasetSource.id
                });
            });
            return datasets;
        };

        this.updateStreamingCharacteristics = function (graphParams) {
            var streamingPlugin = this.chart.options.plugins.streaming;
            streamingPlugin.delay = graphParams.delay;
            streamingPlugin.duration = graphParams.duration;
        };

        this.defaultPixelsPerPointSparse = 25;
        this.calcPointsNumber = function () {
            var pointsNumber = chartElement.width / this.defaultPixelsPerPointSparse;
            return Math.round(pointsNumber);
        };

        this.calcTimeGap = function (timeDuration) {
            return Math.max(Math.round((timeDuration / this.calcPointsNumber())/1000)*1000, 1000);
        };

        this.updateOnline = function(graphParams) {
            graphParams.timeGap = this.calcTimeGap(Math.abs(graphParams.max - graphParams.min));
            var isChartAbsent = this.chart == null || this.chart.options.plugins.streaming == null;

            if (isChartAbsent) {
                this.initGraph(chartElement, graphData, graphParams);
            }
            this.updateStreamingCharacteristics(graphParams);
            this.chart.update();

            this.dataLoader.switchOnline(graphParams.timeGap);
            if (chartElement.width == 0)
                return;
            this.dataLoader.getRange(graphParams.min, graphParams.max, this.calcPointsNumber(),
                this.onDataSetRequestComplete.bind(this));
        };

        function toChartPoints(serverPoints) {
            var roundMs = 1000;
            var unorderedPoints = serverPoints.map((serverPoint) =>
                { return {x: serverPoint.time - serverPoint.time % roundMs, y: serverPoint.value}});
            unorderedPoints.sort((point1, point2) => {return point1.x - point2.x;});
            return unorderedPoints;
        }

        this.onDataSetRequestComplete = function (jsonData) {
            for(var i in jsonData.sampleSet) {
                var matchedDataset = this.chart.data.datasets.find(
                    (dataset) => {return dataset.datasetId == jsonData.sampleSet[i].id});
                if (matchedDataset == null)
                    continue;
                matchedDataset.data = toChartPoints(jsonData.sampleSet[i].values);
            }
            this.chart.update();
        };

        this.setXAxesRange = function (dataRange) {
            var xAxes = this.chart.options.scales.xAxes[0];
            xAxes.type = "time";
            xAxes.time.min = moment(dataRange.min).toDate();
            xAxes.time.max = moment(dataRange.max).toDate();
        };
        
        this.updateOffline = function (dataRange) {
            if (this.chart.options.plugins.streaming) {
                this.chart.options.plugins.streaming = null;
            }
            this.dataLoader.switchOffline();
            this.dataLoader.getRange(dataRange.min, dataRange.max, this.calcPointsNumber(),
                this.onDataSetRequestComplete.bind(this));
            this.setXAxesRange(dataRange);
            this.chart.update();
        };

        function isNullOrEmpty(stringLine) {
            if (stringLine == null)
                return true;
            if (stringLine == "")
                return true;
            return false;
        }

        function formYAxeLabel(axeName, unitName) {
            if (isNullOrEmpty(axeName) || isNullOrEmpty(unitName)){
                if (isNullOrEmpty(axeName))
                   return unitName;
                return axeName;
            }
            return `${axeName}(${unitName})`;
        }

        function addData(graphSampleNodes, targetDatasets) {
            for(var sampleId of Object.keys(graphSampleNodes)) {
                var matchedDataset = targetDatasets.find(
                    (dataset) => {return dataset.datasetId == sampleId;}
                );
                if (matchedDataset == null)
                    continue;
                matchedDataset.data = matchedDataset.data.concat(toChartPoints(graphSampleNodes[sampleId]));
            }
        }

        this.initGraph = function (chartElement, graphData, graphParams) {
            var ctx = chartElement.getContext('2d');

            this.chart = new Chart(ctx, {
                type: 'line',
                data: {
                    datasets: generateDatasets(graphData, graphParams)
                },
                options: {
                    responsive: true,
                    scales: {
                        xAxes: [{
                            type: 'realtime',
                            display: true
                        }],
                        yAxes: [{
                            display: true,
                            scaleLabel: {
                                display: true,
                                labelString:  formYAxeLabel(graphData.yaxisName, graphData.yaxisUnitName)
                            }
                        }]
                    },
                    plugins: {
                        streaming: {
                            onRefresh: function(chart) {
                                addData(chart.dataLoader.getOnlineCachedData(), chart.data.datasets);
                                chart.update();
                            },
                            refresh: moment.duration(1, "seconds").valueOf()
                        }
                    }
                }
            });
            this.chart.dataLoader = this.dataLoader;
        };
        this.dataLoader = new GraphDataLoader(graphData.id, timeDifference);

        this.destroy = function () {
            this.chart.options.plugins.streaming = false;
            this.chart.destroy();
            this.dataLoader.destroy();
        };

    }
});