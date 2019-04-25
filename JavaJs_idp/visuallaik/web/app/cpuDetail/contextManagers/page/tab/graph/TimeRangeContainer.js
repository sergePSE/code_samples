sap.ui.define([],
    function () {
        return function () {
            this.dataRanges = []; // [{min, max, values: {time: value}}]

            function areRangeCrossed(dataRange1, dataRange2){
                function isRangeCrossed(dataRange1, dataRange2) {
                    return (dataRange1.min > dataRange2.min && dataRange1.min < dataRange2.max) ||
                        (dataRange1.max > dataRange2.min && dataRange1.max < dataRange2.max);
                };

                return isRangeCrossed(dataRange1, dataRange2) || isRangeCrossed(dataRange2, dataRange1);
            };

            function mergeRanges(range1, range2) {
                if (range1.min < range2.min){
                    var minRange = range1.min;
                    var maxRange = range2.min;
                } else {
                    var minRange = range2.min;
                    var maxRange = range1.min;
                }
                return {
                    min: [range1.min, range2.min].min(),
                    max: [range1.max, range2.max].max(),
                    values: Object.assign({}, minRange.values, maxRange.values)
                }
            };

            this.addRange = function(dataRange) {
                var crossedDataRanges = this.dataRanges.filter(function (existDataRange) {
                    return areRangeCrossed(existDataRange, dataRange);
                });
                // merge crossed data ranges and remove the old ones
                crossedDataRanges.forEach((crossedDataRange) => {
                    var indexOfDataRange = this.dataRanges.indexOf(crossedDataRange);
                    dataRange = mergeRanges(crossedDataRange, dataRange);
                    this.dataRanges.splice(indexOfDataRange, 1);
                });
                this.dataRanges.push(dataRange);
                this.dataRanges = this.dataRanges.sort((r1, r2) => r1.min > r2.min);
            };

            this.addRanges = (dataRanges) => dataRanges.forEach(this.addRange);

            function excludeRange(baseRange, excludeRange){
                if (excludeRange.min <= baseRange.min && excludeRange.max >= baseRange.max)
                    return null;
                if (excludeRange.max > baseRange.min)
                    return [{
                        min: excludeRange.max,
                        max: baseRange.max
                    }];
                if (excludeRange.min < baseRange.max)
                    return [{
                        min: baseRange.min,
                        max: excludeRange.min
                    }];
                return [{min: baseRange.min, max: excludeRange.min},
                    {min: excludeRange.max, max: baseRange.max}];
            };

            this.getMissingRanges = function (dataRange) {
                var withinRanges = this.dataRanges.filter((existDataRange) => areRangeCrossed(dataRange, existDataRange));
                var missingRanges = [];
                withinRanges.forEach(withinRange => {
                    var excludeResult = excludeRange(dataRange, withinRange);
                    // exclude range overlaps the whole range
                    if (excludeResult == null)
                        return [];
                    if (excludeResult.length)
                        excludeResult.forEach(excludeResult => missingRanges.push(excludeResult));
                });
                if (!withinRanges.length)
                    return [dataRange];
                return missingRanges;
            };

            this.getDateRange = function (fromDate, toDate) {
                var foundDateValues = this.dataRanges.find(
                    dataRange => dataRange.min <= fromDate && dataRange.max >= toDate
                );
                var newDateRange = {};
                Object.keys(foundDateValues.values).forEach(dateValue => {
                    if (dateValue >= fromDate && dateValue <= toDate)
                        newDateRange[dateValue] = foundDateValues.values[dateValue];
                });
                return newDateRange;
            };
        }
    });