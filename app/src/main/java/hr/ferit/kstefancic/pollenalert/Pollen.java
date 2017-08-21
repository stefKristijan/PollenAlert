package hr.ferit.kstefancic.pollenalert;

/**
 * Created by Kristijan on 18.8.2017..
 */

public class Pollen {
    private int id;
    private String name, category;

    public Pollen(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
