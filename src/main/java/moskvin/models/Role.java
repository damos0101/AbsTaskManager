package moskvin.models;

public enum Role {
    ROLE_1(1, "VIEWER"),
    ROLE_2(2, "EDITOR");

    private int id;
    private String name;

    Role(int id, String name){
        this.id=id;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
