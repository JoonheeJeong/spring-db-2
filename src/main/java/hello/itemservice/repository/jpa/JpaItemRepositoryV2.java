package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JpaItemRepositoryV2 implements ItemRepository {

    private final SpringDataJpaItemRepository repository;

    @Override
    public Item save(Item item) {
        return repository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item item = repository.findById(itemId).orElseThrow();
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        boolean isEmptyItemName = StringUtils.isEmptyOrWhitespace(cond.getItemName());
        boolean isNullMaxPrice = cond.getMaxPrice() == null;

        if (!isEmptyItemName) {
            final String itemName = "%" + cond.getItemName() + "%";
            if (isNullMaxPrice) {
                return repository.findByItemNameLike(itemName);
            }
//            return repository.findByItemNameLikeAndPriceLessThanEqual(itemName, cond.getMaxPrice());
            return repository.findItems(itemName, cond.getMaxPrice());
        } else if (isNullMaxPrice) {
            return repository.findAll();
        } else {
            return repository.findByPriceLessThanEqual(cond.getMaxPrice());
        }
    }
}
