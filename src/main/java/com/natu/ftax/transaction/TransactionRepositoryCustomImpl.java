package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Transaction> findAllByClientWithFilter(Client client, Filter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        // Mandatory predicate: client
        predicates.add(cb.equal(transaction.get("client"), client));

        // Optional predicates based on the filter
        if (filter.getValid() != null) {
            predicates.add(cb.equal(transaction.get("valid"), filter.getValid()));
        }

        if (filter.getFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("localDateTime"), filter.getFrom()));
        }

        if (filter.getTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(transaction.get("localDateTime"), filter.getTo()));
        }

        //        if (filter.getTokens() != null && !filter.getTokens().isEmpty()) {
        //            // Assuming 'tokens' is a collection attribute in Transaction
        //            Join<Transaction, String> tokensJoin = transaction.join("token");
        //            predicates.add(tokensJoin.in(filter.getTokens()));
        //            // To avoid duplicates due to joins
        //            query.distinct(true);
        //        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.orderBy(cb.asc(transaction.get("localDateTime")));

        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}


