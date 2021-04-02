package top.codewood.service.bean.page;

import java.io.Serializable;
import java.util.List;

public class PageEntity<T> implements Serializable {

    private int page;
    private int size;
    private List<T> data;
    private int totalPages;
    private int totalElements;

    public PageEntity() {}

    public PageEntity(PageInfo pageInfo) {
        this.page = pageInfo.getPage();
        this.size = pageInfo.getSize();
    }

    public PageEntity(PageInfo pageInfo, List<T> data, long totalElements) {
        this(pageInfo);
        this.data = data;
        this.totalElements = ((Long)totalElements).intValue();
        this.totalPages = this.totalElements == 0 ? 1 : (int) Math.ceil((double) this.totalElements / (double) pageInfo.getSize());
    }

    public PageEntity(int page, int size, List<T> data, long totalElements) {
        this.page = page;
        this.size = size;
        this.data = data;
        this.totalElements = ((Long)totalElements).intValue();
        this.totalPages = this.totalElements == 0 ? 1 : (int) Math.ceil((double) this.totalElements / (double) size);
    }

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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
