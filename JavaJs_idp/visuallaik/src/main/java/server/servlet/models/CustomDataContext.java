package server.servlet.models;

public class CustomDataContext implements IPlacedContext {
    private long id;
    private String name;
    private CustomDataValue dataValue;
    private String placement;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomDataValue getDataValue() {
        return dataValue;
    }

    public void setDataValue(CustomDataValue dataValue) {
        this.dataValue = dataValue;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }
}
