sap.ui.define(function () {
    // colors generator for chart lines
    // background lines have additional colors
    return {
        getBorderColor: function(i)
        {
            return "rgb(" + this.colors[i % this.colors.length][0] + ")";
        },
        getBackgroundColor: function (i) {
            return "rgba(" + this.colors[i % this.colors.length][1] + ", 0.5)";
        },
        colors: [
            ["57, 106, 177", "114, 147, 203"], 
            ["204, 37, 41", "211, 94, 96"],
            ["62, 150, 81", "132, 186, 91"]
        ]
    }
});
