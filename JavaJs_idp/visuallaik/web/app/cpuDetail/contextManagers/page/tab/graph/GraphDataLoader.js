sap.ui.define(["sap/ui/tum/grid/cpu/app/cpuDetail/contextManagers/page/tab/graph/TimeRangeContainer"],
    // loads a data for charts
    function (TimeRangeContainer) {
        return function (contextId, timeDifference) {
            // cache for data, that came from web sockets
            this.onlineCache = {};

            this.getRange = function (fromDate, toDate, pointCount, onRequestComplete) {
                var url = window.location.origin + "/rest/chartData";

                // shift date to correct with respect of the local and server time (use server time)
                jQuery.get(url, {id : this.contextId, from: Math.round(fromDate - this.timeDifference),
                        to : Math.round(toDate - timeDifference), nodesNumber : pointCount},
                    function(stringData) {
                        var jsonData = JSON.parse(stringData);
                        jsonData.sampleSet.sort((set1, set2) => {return set1.id - set2.id;});
                        jsonData.sampleSet.forEach(sampleSet =>
                            sampleSet.values.forEach(graphData => {graphData.time += timeDifference;}));
                        onRequestComplete(jsonData);
                    });
            };
            function onDataArrived(dataString, graphSampleDictionary, timeDifference) {
                var data = JSON.parse(dataString);
                if (data == null)
                    return;
                for (var i = 0; i < data.graphSampleData.length; i++) {
                    var graphSampleId = data.graphSampleData[i].graphSampleId;
                    if (graphSampleDictionary[graphSampleId] == null)
                        graphSampleDictionary[graphSampleId] = [];
                    graphSampleDictionary[graphSampleId].push({
                        time: data.graphSampleData[i].xValue + timeDifference,
                        value: data.graphSampleData[i].yValue
                    });
                }
            }

            // activate websockets for online data
            this.switchOnline = function (timeGap) {
                this.onlineCache = [];

                var cpuStateAddress = "/websocket/graphContext";
                this.closeSocket();

                this.socket = new WebSocket("ws://" + window.location.host + cpuStateAddress);
                this.socket.onopen = () =>
                    this.socket.send(JSON.stringify({contextId: this.contextId, period: timeGap}));
                this.socket.onmessage = (event) => {
                    onDataArrived(event.data, this.onlineCache, this.timeDifference);
                };

            };

            this.closeSocket = function () {
                if (this.socket != null) {
                    this.socket.close();
                    this.socket = null;
                }
            };

            this.switchOffline = function () {
                if (this.socket != null) {
                    this.socket.close();
                    this.socket = null;
                }
                this.onlineCache = {};
            };
            // get cache data and erase it from store
            this.getOnlineCachedData = function () {
                var cachedValues = this.onlineCache;
                this.onlineCache = {};
                return cachedValues;
            };

            this.destroy = function() {
                if (this.socket != null)
                    this.socket.close();
            };

            this.timeDifference = timeDifference;
            this.contextId = contextId;
        }
    });