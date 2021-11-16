package pl.maya.desktop.util;

public enum DiodeE {
    D240("240"), D255("255"), D270("270"), D285("285");
    private DiodeE(String value){
        this.value = value;
    }
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
