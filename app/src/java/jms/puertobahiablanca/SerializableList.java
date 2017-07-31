package jms.puertobahiablanca;

import java.io.Serializable;
import java.util.List;

public class SerializableList implements Serializable {

    private List<String[]> data;

    public SerializableList(List<String[]> data) {
        this.data = data;
    }

    public List<String[]> getList() {
        return this.data;
    }
}