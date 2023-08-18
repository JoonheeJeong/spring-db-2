package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * NameParameterJdbcTemplate
 * SqlParameterSource
 * BeanPropertyRowMapper
 */
@Repository
public class JdbcTemplateRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        final String sql = "insert into item (item_name, price, quantity) values (:itemName, :price, :quantity)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        template.update(sql, param, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        item.setId(id);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        final String sql = "update item set item_name = :itemName, " +
                                           "price = :price, " +
                                           "quantity = :quantity " +
                           "where id = :id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        final String sql = "select * from item where id = :id";
        Map<String, Long> id1 = Map.of("id", id);
        Item item = template.queryForObject(sql, id1, rowMapper());
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        StringBuilder sqlBuilder = new StringBuilder("select * from item");
        StringBuilder whereBuilder = new StringBuilder();
        boolean needAnd = false;
        if (!StringUtils.isEmpty(cond.getItemName())) {
            String pattern = "'%" + cond.getItemName() + "%'";
            whereBuilder.append(" where item_name like ")
                        .append(pattern);
            needAnd = true;
        }
        if (cond.getMaxPrice() != null) {
            if (needAnd) {
                whereBuilder.append(" and ");
            } else {
                whereBuilder.append(" where ");
            }
            whereBuilder.append("price <= :maxPrice");
        }
        sqlBuilder.append(whereBuilder);

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);
        return template.query(sqlBuilder.toString(), param, rowMapper());
    }

    @Override
    public void clear() {
        template.update("delete from item", new EmptySqlParameterSource());
    }

    private RowMapper<Item> rowMapper() {
        return new BeanPropertyRowMapper<>(Item.class);
    }
}
