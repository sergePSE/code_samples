sap.ui.define([], function () {
    return function(parentControl, custom_data){
        this._refresh = function(custom_data) {
            this.textBox.setText(`${custom_data.name}: ${custom_data.dataValue.value}`);
        };

        this.destroy = function() {
            this.textBox.getParent().removeItem(this.textBox);
            this.textBox.destroy();
        };

        this.createContext = function(parentPanel, customData) {
            this.textBox = new sap.m.Text();
            parentPanel.addItem(this.textBox);
            this._refresh(customData);
        };

        this.parentControl = parentControl;
        this.createContext(this.parentControl, custom_data);
    };
});