package model;

import java.util.List;

public class Classification {

    private String class_name;
    private List<String[]> subclass;

    public Classification(String class_name, List<String[]> subclass) {
        this.class_name = class_name;
        this.subclass = subclass;
    }

    public String getClass_name() {
        return class_name;
    }

    public List<String[]> getSubclass() {
        return subclass;
    }

}
