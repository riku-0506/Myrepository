package application;

public class Abnormality {
    private String name;
    private String description;
    private String code; // 任意（null可）

    public Abnormality(String name, String description) {
        this.name = name;
        this.description = description;
        this.code = "";
    }

    public Abnormality(String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
}