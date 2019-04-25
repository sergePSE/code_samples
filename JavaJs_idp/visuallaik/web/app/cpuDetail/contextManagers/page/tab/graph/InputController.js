sap.ui.define(["sap/ui/tum/grid/cpu/resources/moment/moment.min"], function (momentjs) {
    // chart input controller
    return function (panelContainer, graphData, chartController, timeDiffrence) {
        // get controls to handle it manually
        this.findControls = function(panelContainer) {
            this.htmlElements = {
                fromDatePicker: panelContainer.getContent()[0].getItems()[3].getContent()[0],
                toDatePicker: panelContainer.getContent()[0].getItems()[3].getContent()[2],
                dateRangeSlider: panelContainer.getContent()[0].getItems()[2],
                isOnlineCheckBox: panelContainer.getContent()[0].getItems()[1],
                dateTimeToolbar: panelContainer.getContent()[0].getItems()[3],
                title: panelContainer.getHeaderToolbar().getContent()[0]
            };
            // subscribe on change events
            this.htmlElements.isOnlineCheckBox.attachSelect(this.onOnlineSelect, this);
            this.htmlElements.dateRangeSlider.attachLiveChange(this.onSliderChange, this);
            this.htmlElements.fromDatePicker.attachChange(this.onSliderChange, this);
            this.htmlElements.toDatePicker.attachChange(this.onSliderChange, this);
        };

        this.onSliderChange = function () {
            // get corresponding dates from slider range. range is within 0.1-100%
            var dateRangeSlider = this.htmlElements.dateRangeSlider;
            var sliderValues = {
                left: Math.min(dateRangeSlider.getValue(), dateRangeSlider.getValue2()),
                right: Math.max(dateRangeSlider.getValue(), dateRangeSlider.getValue2())
            };
            // choose the handle method
            if (this.isOnline)
                this.onOnlineSliderChange(sliderValues);
            else
                this.onOfflineSliderChange(sliderValues);
        };

        // for online the default full range
        this.defaultOnlineTimeRangeMs = moment.duration(1, "hours"); // 12 hours

        this.getSliderRange = function(sliderValues) {
            // get time range using the default value or values of date inputs
            if (this.isOnline) {
                return this.getRange(moment() - this.defaultOnlineTimeRangeMs, moment(), sliderValues);
            } else
            {
                return this.getRange(this.htmlElements.fromDatePicker.getDateValue().valueOf(),
                    this.htmlElements.toDatePicker.getDateValue().valueOf(), sliderValues);
            }

        };

        this.getRange = function(minTime, maxTime, sliderValues){
            var timeRange = maxTime - minTime;
            var sliderValues = {
                left: minTime + (sliderValues.left / 1000.0) * timeRange,
                right: minTime + (sliderValues.right / 1000.0) * timeRange
            };
            return {
                min: Math.min(sliderValues.left, sliderValues.right),
                max: Math.max(sliderValues.left, sliderValues.right)
            };
        };

        this.defaultUpdateTimeToPixelWidth = 5; // width 1000 -> 200 nodes
        this.minUpdateTime = moment.duration(1, "seconds");

        this.onOnlineSliderChange = function(sliderValues) {
            var graphParams = {
                duration: ((sliderValues.right - sliderValues.left) / 1000.0) * this.defaultOnlineTimeRangeMs,
                delay: ((1000.0 - sliderValues.right) / 1000.0) * this.defaultOnlineTimeRangeMs
            };
            chartController.updateOnline(Object.assign({}, graphParams, this.getSliderRange(sliderValues)));
        };

        this.onOfflineSliderChange = function(sliderValues) {
            chartController.updateOffline(this.getSliderRange(sliderValues));
        };

        this.updateDateInput = function () {
            this.htmlElements.dateTimeToolbar.setVisible(!this.isOnline);
            if (this.isOnline) {
                var now = moment();
                this.htmlElements.toDatePicker.setDateValue(now.toDate());
                this.htmlElements.fromDatePicker.setDateValue(moment(now - this.defaultOnlineTimeRangeMs).toDate());
            }
            this.areDateInputsEnabled = !this.isOnline;
        };

        this.setDefaultSliderRange = function () {
            // set 100 % default range
            this.htmlElements.dateRangeSlider.setValue(0);
            this.htmlElements.dateRangeSlider.setValue(1000);
        };

        this.onOnlineSelect = function () {
            this.isOnline = !this.isOnline;
            this.updateDateInput();
            this.onSliderChange();
        };

        this.isOnline = true;
        this.areDateInputsEnabled = false;
        this.findControls(panelContainer);
        this.htmlElements.title.setText(graphData.name);
        this.updateDateInput();
        this.timeDifference = timeDiffrence;
        this.onSliderChange();
    }
});