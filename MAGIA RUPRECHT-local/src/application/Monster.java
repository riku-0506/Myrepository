package application;

public class Monster {
    private final String name;
    private final String description;
    private final String code;
    private final String iconPath;

    public Monster(String name, String description, String code, String iconPath) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.iconPath = iconPath;
    }

    public String getIconPath() { return iconPath; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCode() { return code; }

}
