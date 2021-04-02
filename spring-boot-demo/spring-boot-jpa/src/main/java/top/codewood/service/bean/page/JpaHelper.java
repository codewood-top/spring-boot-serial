package top.codewood.service.bean.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;

public class JpaHelper {

    public final static <T> Specification<T> getSpecification(PageInfo pageInfo) {
        return new Specification<T>() {

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                pageInfo.getWhereCauseMap().entrySet().stream().forEach(kv -> {
                    predicateList.add(cb.equal(root.get(kv.getKey()), kv.getValue()));
                });
                pageInfo.getSearchCauseMap().entrySet().stream().forEach(kv -> {
                    predicateList.add(cb.like(root.get(kv.getKey()), "%" + kv.getValue() + "%"));
                });
                pageInfo.getInCauseMap().entrySet().stream().forEach(kv -> {
                    CriteriaBuilder.In<Serializable> ins = cb.in(root.get(kv.getKey()));
                    kv.getValue().stream().forEach(v -> ins.value(v));
                    predicateList.add(ins);
                });

                pageInfo.getCompareModels().forEach(compareModel -> {
                    if (compareModel.getName() == null || compareModel.getName().trim() == "" || compareModel.getValue() == null || compareModel.getOpt() == null) return;

//					String value = null;
//					if (compareModel.getValue() instanceof Date) {
//						value =  DateFormatUtils.format((Date) compareModel.getValue(), "yyyy-MM-dd HH:mm:ss");
//					} else {
//						value = String.valueOf(compareModel.getValue());
//					}

                    Path<Date> dateParamName = null;
                    Date dateParamVal = null;

                    if (compareModel.getValue() instanceof Date) {
                        dateParamName = root.<Date>get(compareModel.getName());
                        dateParamVal = (Date) compareModel.getValue();
                    }

                    switch (compareModel.getOpt().getValue()) {
                        case "=" :
                            predicateList.add(cb.equal(root.get(compareModel.getName()), compareModel.getValue()));
                            break;
                        case "<>":
                            predicateList.add(cb.notEqual(root.get(compareModel.getName()), compareModel.getValue()));
                            break;
                        case ">=":
                            if (dateParamVal != null) {
                                predicateList.add(cb.greaterThanOrEqualTo(dateParamName, dateParamVal));
                            } else {
                                predicateList.add(cb.greaterThanOrEqualTo(root.get(compareModel.getName()), String.valueOf(compareModel.getValue())));
                            }

                            break;
                        case ">":
                            if (dateParamVal != null) {
                                predicateList.add(cb.greaterThan(dateParamName, dateParamVal));
                            } else {
                                predicateList.add(cb.greaterThan(root.get(compareModel.getName()), String.valueOf(compareModel.getValue())));
                            }
                            break;
                        case "<=":
                            if (dateParamVal != null) {
                                predicateList.add(cb.lessThanOrEqualTo(dateParamName, dateParamVal));
                            } else {
                                predicateList.add(cb.lessThanOrEqualTo(root.get(compareModel.getName()), String.valueOf(compareModel.getValue())));
                            }
                            break;
                        case "<":
                            if (dateParamVal != null) {
                                predicateList.add(cb.lessThan(dateParamName, dateParamVal));
                            } else {
                                predicateList.add(cb.lessThan(root.get(compareModel.getName()), String.valueOf(compareModel.getValue())));
                            }
                            break;
                        default:
                            break;
                    }
                });

                if (predicateList.size() == 0) {
                    return null;
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                return cb.and(predicateList.toArray(predicates));
            }
        };
    }

    public final static <T> Specification<T> getSpecification(Map<String, Object> whereCauseMap) {
        return new Specification<T>() {

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                whereCauseMap.entrySet().stream().forEach(kv -> {
                    predicateList.add(cb.equal(root.get(kv.getKey()), kv.getValue()));
                });
                if (predicateList.size() == 0) {
                    return null;
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                return cb.and(predicateList.toArray(predicates));
            }
        };
    }

    /**
     *
     * @param alias          别名 如 t.name 的 t
     * @param pageInfo
     * @param isWithWhereStr 是否需要带上 where
     * @return
     */
    public final static String getNativeQueryCauseString(String alias, PageInfo pageInfo, boolean isWithWhereStr) {
        if (pageInfo == null || (pageInfo.getWhereCauseMap() == null || pageInfo.getWhereCauseMap().size() == 0) &&
                (pageInfo.getSearchCauseMap() == null || pageInfo.getSearchCauseMap().size() == 0)
                && (pageInfo.getInCauseMap() == null || pageInfo.getInCauseMap().size() == 0))
            return "";

        StringBuilder sb = new StringBuilder();
        if (isWithWhereStr) sb.append(" where "); else sb.append(" and ");

        boolean shouldAttachAndStr = false;

        if (pageInfo.getWhereCauseMap().size() > 0) {

            Set<Map.Entry<String, Object>> entrySet = pageInfo.getWhereCauseMap().entrySet();
            int i = 0, size = entrySet.size();
            for (Map.Entry<String, Object> entry : entrySet) {
                i++;
                sb.append(alias).append(".").append(entry.getKey()).append("=");
                if (entry.getValue() instanceof String) {
                    sb.append("'").append(entry.getValue()).append("'");
                } else {
                    sb.append(entry.getValue());
                }
                sb.append(" ");
                if (i != size) {
                    sb.append(" and ");
                }
            }
            shouldAttachAndStr = true;
        }

        if (pageInfo.getInCauseMap().size() > 0) {

            if (shouldAttachAndStr) {
                sb.append(" and ");
            }

            Set<Map.Entry<String, List<? extends Serializable>>> entrySet = pageInfo.getInCauseMap().entrySet();
            int i = 0, size = entrySet.size();
            for (Map.Entry<String, List<? extends Serializable>> entry : entrySet) {
                i++;
                sb.append(alias).append(".").append(entry.getKey()).append(" in (").append(entry.getValue()).append(") ");
                if (i != size) {
                    sb.append(" and ");
                }
            }
            shouldAttachAndStr = true;
        }

        if (pageInfo.getSearchCauseMap().size() > 0) {
            if (shouldAttachAndStr) {
                sb.append(" and ");
            }

            Set<Map.Entry<String, Object>> entrySet = pageInfo.getSearchCauseMap().entrySet();
            int i = 0, size = entrySet.size();
            for (Map.Entry<String, Object> entry : entrySet) {
                i++;
                sb.append(alias).append(".").append(entry.getKey()).append(" like '%").append(entry.getValue()).append("%' ");
                if (i != size) {
                    sb.append(" and ");
                }
            }
        }

        return sb.toString();
    }

    public final static Pageable getPageable(PageInfo pageInfo) {
        return PageRequest.of(pageInfo.getPage() - 1, pageInfo.getSize(),
                Sort.by(pageInfo.getSort().toLowerCase().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        pageInfo.getSortName()));
    }

    public final static <T> PageEntity<T> pageEntity(PageInfo pageInfo, Page<T> page) {
        return new PageEntity<T>(pageInfo, page.getContent(), page.getTotalElements());
    }

    public final static <T> T getSingleResultOrNull(TypedQuery<T> query) {
        query.setMaxResults(1);
        List<T> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
