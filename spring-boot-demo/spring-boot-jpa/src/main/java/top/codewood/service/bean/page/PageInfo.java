package top.codewood.service.bean.page;

import java.io.Serializable;
import java.util.*;

public class PageInfo extends PageVO {

    public static final String SORT_DESC = "desc";
    public static final String SORT_ASC = "asc";

    private Map<String, Object> whereCauseMap; // 条件

    private Map<String, Object> searchCauseMap; // 查询条件

    private Map<String, List<? extends Serializable>> inCauseMap;  // 条件 in

    private Map<String, String> sortMap;

    private List<CompareModel> compareModels; // 比较列表

    public PageInfo() {}

    public PageInfo(PageVO pageVO) {
        this.setPage(pageVO.getPage());
        this.setSize(pageVO.getSize());
        this.setSort(pageVO.getSort());
        this.setSortName(pageVO.getSortName());
    }

    public int getStartPosition() {
        return (getPage() - 1) * getSize();
    }

    public Map<String, Object> getWhereCauseMap() {
        if (whereCauseMap == null) {
            whereCauseMap = new HashMap<>();
        }
        return whereCauseMap;
    }

    public void setWhereCauseMap(Map<String, Object> whereCauseMap) {
        this.whereCauseMap = whereCauseMap;
    }

    public void addWhereCause(String key, Object val) {
        if (whereCauseMap == null) {
            whereCauseMap = new HashMap<>();
        }
        whereCauseMap.put(key, val);
    }

    public void clearWhereCause() {
        if (whereCauseMap != null) {
            whereCauseMap.clear();
        }
    }

    public Map<String, Object> getSearchCauseMap() {
        if (searchCauseMap == null) {
            searchCauseMap = new HashMap<>();
        }
        return searchCauseMap;
    }

    public void setSearchCauseMap(Map<String, Object> searchCauseMap) {
        this.searchCauseMap = searchCauseMap;
    }

    public void addSearchCause(String key, Object val) {
        if (searchCauseMap == null) {
            searchCauseMap = new HashMap<>();
        }
        searchCauseMap.put(key, val);
    }

    public void clearSearchMap() {
        if (searchCauseMap != null) {
            searchCauseMap.clear();
        }
    }

    public Map<String, String> getSortMap() {
        if (sortMap == null) {
            sortMap = new LinkedHashMap<>();
            sortMap.put(this.getSortName(), this.getSort());
        }

        return sortMap;
    }

    public void addSortCause(String sortName, String sort) {
        if (sortMap == null) {
            sortMap = new LinkedHashMap<>();
        }
        sortMap.put(sortName, sort);
    }

    public Map<String, List<? extends Serializable>> getInCauseMap() {
        if (inCauseMap == null) {
            inCauseMap = new HashMap<>();
        }
        return inCauseMap;
    }

    public void setInCauseMap(Map<String, List<? extends Serializable>> inCauseMap) {
        this.inCauseMap = inCauseMap;
    }

    public void addInCause(String key, List<? extends Serializable> val) {
        if (inCauseMap == null) {
            inCauseMap = new HashMap<>();
        }
        inCauseMap.put(key, val);
    }

    public void clearInCauseMap() {
        if (inCauseMap != null) {
            inCauseMap.clear();
        }
    }

    public List<CompareModel> getCompareModels() {
        if (compareModels == null) this.compareModels = new ArrayList<>();
        return compareModels;
    }

    public void setCompareModels(List<CompareModel> compareModels) {
        this.compareModels = compareModels;
    }

    public void addCompareModel(CompareModel compareModel) {
        List<CompareModel> compareModels = getCompareModels();
        compareModels.add(compareModel);
    }

    public static class CompareModel implements Serializable {
        private String name;
        private Object value;
        private Opt opt;

        public CompareModel(String name, Object value, Opt opt) {
            this.name = name;
            this.value = value;
            this.opt = opt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Opt getOpt() {
            return opt;
        }

        public void setOpt(Opt opt) {
            this.opt = opt;
        }
    }

    public enum Opt {
        EQUAL("等于", "="),
        NOT_EQUAL("不等于", "<>"),
        GREATER_OR_EQUAL_TO("大于等于", ">="),
        GREATER("大于", ">"),
        LESS_OR_EQUAL_TO("小于等于", "<="),
        LESS("小于", "<");

        private String name;
        private String value;

        Opt(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static PageInfo of(PageVO pageVO) {
        return new PageInfo(pageVO);
    }

}
