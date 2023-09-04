package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcTemplateRepositoryV1 implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateRepositoryV1(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        final String sql = "insert into item (item_name, price, quantity) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(sql, new String[] {"id"});
            pstmt.setString(1, item.getItemName());
            pstmt.setInt(2, item.getPrice());
            pstmt.setInt(3, item.getQuantity());
            return pstmt;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        item.setId(id);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        final String sql = "update item set item_name = ?, price = ?, quantity = ? where id = ?";
        jdbcTemplate.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        final String sql = "select * from item where id = ?";
        Item item = jdbcTemplate.queryForObject(sql, rowMapper(), id);
        return Optional.ofNullable(item);
    }

    private RowMapper<Item> rowMapper() {
        return ((rs, rowNum) -> {
            Item item = new Item(
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4)
            );
            item.setId(rs.getLong(1));
            return item;
        });
    }

    /**
     * 쿼리에서 다 가져와서 리턴만 조정한다는 비효율적인 문제가 있음
     * 요런 부분에서 동적 쿼리 필요할 듯
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
//        // 1. 전체 쿼리 후 람다로 필터링
//        final String sql = "select * from item";
//        return jdbcTemplate.query(sql, rowMapper()).stream()
//                .filter(item -> StringUtils.isEmpty(cond.getItemName())
//                                || item.getItemName().contains(cond.getItemName()))
//                .filter(item -> Objects.isNull(cond.getMaxPrice())
//                                || item.getPrice() <= cond.getMaxPrice())
//                .collect(Collectors.toList());

        // 2. 동적쿼리
        StringBuilder sqlBuilder = new StringBuilder("select * from item");

        boolean needAnd = false;
        List<Object> args = new ArrayList<>(1);
        StringBuilder whereBuilder = new StringBuilder();

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
            whereBuilder.append("price <= ?");
            args.add(cond.getMaxPrice());
        }

        sqlBuilder.append(whereBuilder);
        return jdbcTemplate.query(sqlBuilder.toString(), rowMapper(), args.toArray());
    }

//    public void clear() {
//        jdbcTemplate.update("delete from item");
//    }
}
