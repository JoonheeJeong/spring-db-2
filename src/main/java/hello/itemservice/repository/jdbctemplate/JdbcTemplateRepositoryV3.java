package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SimpleJdbcInsert
 */
@Repository
public class JdbcTemplateRepositoryV3 implements ItemRepository {

    private final RowMapper<Item> itemRowMapper = new BeanPropertyRowMapper<>(Item.class);
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateRepositoryV3(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id")
//                .usingColumns("item_name", "price", "quantity") // insert 할 때 필요한 컬럼 지정
                ;
    }

    @Override
    public Item save(Item item) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(item);

        Number number = jdbcInsert.executeAndReturnKey(param);

        item.setId(number.longValue());
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
        Item item = template.queryForObject(sql, id1, itemRowMapper);
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
        return template.query(sqlBuilder.toString(), param, itemRowMapper);
    }

    @Override
    public void clear() {
        template.update("delete from item", new EmptySqlParameterSource());
    }
}
