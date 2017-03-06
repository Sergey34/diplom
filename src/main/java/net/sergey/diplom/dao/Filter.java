package net.sergey.diplom.dao;

import net.sergey.diplom.dto.Condition;

import java.util.ArrayList;
import java.util.List;

public class Filter implements Specification {
    private List<Condition> conditions;

    public Filter(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, criteriaQuery, criteriaBuilder);
        return predicates.size() > 1
                ? criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]))
                : predicates.get(0);
    }

    private List<Predicate> buildPredicates(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Condition condition : conditions) {
            predicates.add(buildPredicate(condition, root, criteriaBuilder));
        }
        return predicates;
    }

    private Predicate buildPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        switch (condition.getAction()) {
            case "<":
                return buildGqPredicateToCriteria(condition, root, criteriaBuilder);
            case ">":
                return buildLqPredicateToCriteria(condition, root, criteriaBuilder);
            case "=":
                return buildEqualsPredicateToCriteria(condition, root, criteriaBuilder);
            case "=="://contains
                return buildContainsPredicateToCriteria(condition, root, criteriaBuilder);
        }
        return null;
    }

    private Predicate buildLqPredicateToCriteria(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lessThanOrEqualTo(root.get(condition.getAttrName()), condition.getValue());
    }

    private Predicate buildGqPredicateToCriteria(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get(condition.getAttrName()), condition.getValue());
    }

    private Predicate buildContainsPredicateToCriteria(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(root.get(condition.getAttrName()), condition.getValue());
    }

    private Predicate buildEqualsPredicateToCriteria(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(condition.getAttrName()), condition.getValue());
    }


}
