package net.sergey.diplom.dao;

import net.sergey.diplom.dto.Condition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class Filter {
    public Query toQuery(List<Condition> conditions) {
        Criteria criteria = new Criteria();
        for (Condition condition : conditions) {
            criteria.andOperator(buildCriteria(condition));
        }
        return Query.query(criteria);
    }

    private Criteria buildCriteria(Condition condition) {
        switch (condition.getAction()) {
            case "<":
                return buildGqCriteriaToCriteria(condition);
            case ">":
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