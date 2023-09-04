package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JpaItemRepositoryV1 implements ItemRepository {

    private final EntityManager em;

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item item = em.find(Item.class, itemId);
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        boolean isEmptyItemName = StringUtils.isEmptyOrWhitespace(cond.getItemName());
        boolean isNullMaxPrice = cond.getMaxPrice() == null;
        final String jpql = getDynamicQueryForFindAll(isEmptyItemName, isNullMaxPrice);
        log.info("jpql: {}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (!isEmptyItemName) {
            query.setParameter("itemName", cond.getItemName());
        }
        if (!isNullMaxPrice) {
            query.setParameter("maxPrice", cond.getMaxPrice());
        }
        return query.getResultList();
    }

    private String getDynamicQueryForFindAll(boolean isEmptyItemName, boolean isNullMaxPrice) {
        StringBuilder sqlBuilder = new StringBuilder("select i from Item i");
        if (isEmptyItemName && isNullMaxPrice) {
            return sqlBuilder.toString();
        }

        StringBuilder whereBuilder = new StringBuilder(" where ");
        boolean needAnd = false;
        if (!isEmptyItemName) {
            whereBuilder.append("item_name like concat('%', :itemName, '%')");
            needAnd = true;
        }
        if (!isNullMaxPrice) {
            if (needAnd) {
                whereBuilder.append(" and ");
            }
            whereBuilder.append("price <= :maxPrice");
        }
        return sqlBuilder.append(whereBuilder).toString();
    }
}
