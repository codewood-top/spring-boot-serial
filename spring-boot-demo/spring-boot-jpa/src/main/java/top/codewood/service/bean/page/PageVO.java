package top.codewood.service.bean.page;

import java.io.Serializable;

public class PageVO implements Serializable {

    private int page = 1;
    private int size = 10;
    private String sortName = "id";
    private String sort = "desc";

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
