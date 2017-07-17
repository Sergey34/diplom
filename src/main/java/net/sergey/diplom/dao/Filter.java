package net.sergey.diplom.dao;

import net.sergey.diplom.dto.Condition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Filter {
    public Query toQuery(List<Condition> conditions) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (Condition condition : conditions) {
            criteriaList.add(buildCriteria(condition));
        }
        Criteria criteria;
        if (criteriaList.size() > 1) {
            criteria = new Criteria().andOperator((Criteria[]) criteriaList.toArray());
        } else {
            criteria = criteriaList.get(0);
        }

        Query query = Query.query(criteria);
        query.fields().include("id");
        return query;
    }

    private Criteria buildCriteria(Condition condition) {
        switch (condition.getAction()) {
            case ">":
                return buildGqCriteriaToCriteria(condition);
            case "<":
                return buildLqCriteriaToCriteria(condition);
            case "=":
                return buildEqualsCriteriaToCriteria(condition);
        }
        return null;
    }

    private Criteria buildLqCriteriaToCriteria(Condition condition) {
        return Criteria.where(condition.getAttrName()).lte(condition.getValue());

    }

    private Criteria buildGqCriteriaToCriteria(Condition condition) {
        return Criteria.where(condition.getAttrName()).gte(condition.getValue());

    }

    private Criteria buildEqualsCriteriaToCriteria(Condition condition) {
        return Criteria.where(condition.getAttrName()).is(condition.getValue());
    }


}