sap.ui.define([], function () {
    return function(baseUrl) {
        this.sendReboot = function (nodeId) {
            sendCommand(nodeId, "reboot");
        };

        this.sendTurnOn = function (nodeId) {
            sendCommand(nodeId, "turnOn");
        };

        this.sendTurnOff = function (nodeId) {
            sendCommand(nodeId, "turnOff");
        };

        function sendCommand(nodeId, commandName) {
            jQuery.post( baseUrl + "/rest/nodes", {id: nodeId, command: commandName},
                function () {
            });
        }
    }
});