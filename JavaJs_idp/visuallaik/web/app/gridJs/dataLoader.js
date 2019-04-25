sap.ui.define(["sap/ui/tum/grid/cpu/app/gridJs/models"], function (models) {
    return {
        loadData : function(url, onDataReceived) {
            var restructureData = function(rawData)
            {
                var restructuredData = {};
                restructuredData.clusterDescription = Object.assign(rawData.clusterDescription);
                restructuredData.timeDifference = rawData.timeDifference;
                restructuredData.topology = models.getGridDescription(rawData.nodes);
                restructuredData.nodes = rawData.nodes.map(rawNode => {
                    return new models.nodeConstructor(rawNode, restructuredData.topology);
                });
                return restructuredData;
            };

            var getRawData = function(url, restructureDataFunc)
            {
                var requestStartTime = Date.now();
                jQuery.get(url, function(stringData) {
                    var requestEndTime = Date.now();
                    var requestTime = requestEndTime - requestStartTime;
                    var jsonData = JSON.parse(stringData);
                    // local time can be different from the server
                    jsonData.timeDifference = Date.now() - jsonData.currentTime + requestTime / 2;
                    // get current time from server and make estimate correction
                    var restructuredData = restructureDataFunc(jsonData);
                    onDataReceived(restructuredData);
                });
            };

            getRawData(url, restructureData);
        },

        subscribeToChanges: function() {
            var host = window.location.host;
            var cpuStateAddress = "/websocket/rpiState";
            this.socket = new WebSocket("ws://" + host + cpuStateAddress);
            this.socket.addEventListener("message", event => {
                sap.ui.getCore().getEventBus().publish("WebSocket", "onNodeStatusChange", JSON.parse(event.data))
            });

        }
    }
})
