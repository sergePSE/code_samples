sap.ui.define(["sap/ui/model/json/JSONModel",
    "sap/ui/tum/grid/cpu/resources/moment/moment.min",
    "sap/ui/tum/grid/cpu/resources/moment/moment-duration-format"],
    function (JSONModel, momentjs, moment_duration_formatjs ) {
        return function(parentControl, initialTaskData){

            this.setTime = function (oModel, startTime){
                oModel.setData({
                        executionTime: moment.duration(moment() - moment(startTime)).format("hh:mm:ss")
                    }, true);
            };

            this.refresh = function(taskData) {
                var taskModel = {
                    name : taskData.name,
                    isExecutable: taskData.executable? "true" : "false",
                    args: taskData.args,
                    ranksStr : taskData.ranks,
                    isKillButtonEnabled: true
                };
                this._jobStartTime = taskData.jobStartTime;
                this._oModel.setData(taskModel, true);
                this.setTime(this._oModel, taskData.jobStartTime);
            };

            this.init = function(){

                // create a view template
                this._panel = sap.ui.xmlfragment("sap.ui.tum.grid.cpu.fragment.Task");
                this._oModel = new JSONModel();
                this._panel.setModel(this._oModel);

                parentControl.addContent(this._panel);

                this.refresh(initialTaskData);
                this._interval = setInterval(() => {
                    this.setTime(this._oModel, this._jobStartTime);
                }, moment.duration(1, "seconds").valueOf());
            };

            this.onKillButtonPress = function () {
                this._oModel.setData({isKillButtonEnabled: false}, true);
                setTimeout(() => { this._oModel.setData({isKillButtonEnabled: true}, true); },
                    moment.duration(3, "seconds").valueOf());
                alert(`Kill job id ${initialTaskData.taskId}`);
            };

            this.destroy = function() {
                if (this._interval) {
                    clearInterval(this._interval);
                    this._interval = null;
                }
                this._panel.destroy();
            };

            this.init();
        };
});