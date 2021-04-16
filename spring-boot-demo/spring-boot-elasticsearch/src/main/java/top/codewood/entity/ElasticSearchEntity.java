package top.codewood.entity;

import java.io.Serializable;
import java.util.Map;

public class ElasticSearchEntity implements Serializable {

    private String id;
    private Map<String, Object> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
