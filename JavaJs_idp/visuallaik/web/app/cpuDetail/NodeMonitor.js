sap.ui.define(["sap/ui/tum/grid/cpu/app/NodePowerPoster",
        "sap/ui/tum/grid/cpu/resources/moment/moment.min",
        "sap/ui/tum/grid/cpu/resources/moment/moment-duration-format"],
        function (NodePowerPoster, momentjs, moment_duration_formatjs) {
    return function (jsonModel, onNodeContextReceived) {
        this.onNodeIdLoad = function (nodeId) {
            this.nodeId = nodeId;
            sap.ui.getCore().getEventBus().subscribe("CpuDetail", "onNodeResponse", onDataReceived, this);
            sap.ui.getCore().getEventBus().subscribe("AppController", "dataChanged", onDataReceived, this);
            // subscribe on element status change
            sap.ui.getCore().getEventBus().subscribe("WebSocket", "onNodeStatusChange", onNodeStatusChange, this);
            sap.ui.getCore().getEventBus().publish("AppController", "nodeRequest");
        };

        function onDataReceived(thread, eventId, data) {
            if (this.nodeId == null)
                return;
            this.nodeData = data.nodes.find((node => {return node.id == this.nodeId;}));
            this.viewData = {
                id: this.nodeData.id,
                name: this.nodeData.name,
                ip: this.nodeData.ip
            };
            jsonModel.setData(this.viewData, true);
            switchOnlineStatus.bind(this)(this.nodeData.online);
            this.nodeData.timeDifference = data.timeDifference;
            onNodeContextReceived(this.nodeData);
        };

        function switchOnlineStatus(isOnline)
        {
            this.viewData.state = onlineToState(isOnline);
            this.viewData.buttons = {
                turnOnVisible: !isOnline,
                turnOffVisible: isOnline,
                rebootVisible: isOnline,
                turnOnEnabled: true,
                turnOffEnabled: true,
                rebootEnabled: true
            };
            jsonModel.setData(this.viewData, true);
        };

        function onNodeStatusChange(thread, eventId, {nodeId, nodeStatus}) {
            if (this.nodeId != nodeId)
                return;
            switchOnlineStatus.bind(this)(nodeStatus);
        };

        function onlineToState(isOnline) {
            return isOnline? "Success" : "Error";
        }

        this.turnOnCommand = function () {
            if (this.nodeData != null) {
                this.nodePowerPoster.sendTurnOn(this.nodeData.id);

                buttonLockDelay((isEnabled) => {
                    this.viewData.buttons.turnOnEnabled = isEnabled;
                    jsonModel.setData({buttons: this.viewData.buttons}, true);}
                );
            }
        };
        this.turnOffCommand = function () {
            if (this.nodeData != null) {
                this.nodePowerPoster.sendTurnOff(this.nodeData.id);

                buttonLockDelay((isEnabled) => {
                    this.viewData.buttons.turnOffEnabled = isEnabled;
                    jsonModel.setData({buttons: this.viewData.buttons}, true);}
                );
            }
        };
        this.rebootCommand = function () {
            if (this.nodeData != null) {
                this.nodePowerPoster.sendReboot(this.nodeData.id);

                buttonLockDelay((isEnabled) => {
                    this.viewData.buttons.rebootEnabled = isEnabled;
                    jsonModel.setData({buttons: this.viewData.buttons}, true);}
                );
            }
        };

        this.setDefaultJsonModel = function()
        {
            this.viewData = {
                buttons: {
                    turnOnVisible: false,
                    turnOffVisible: false,
                    rebootVisible: false,
                }
            };
            jsonModel.setData(this.viewData, true);
        };

        function buttonLockDelay(changeValueFunction) {
            changeValueFunction(false);
            setTimeout(() => { changeValueFunction(true); }, moment.duration(3, "seconds").valueOf());
        };

        this.destroy = function () {

        };

        this.nodePowerPoster = new NodePowerPoster(window.location.origin);
        this.setDefaultJsonModel();
    };

});