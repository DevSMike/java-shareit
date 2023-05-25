package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long ownerId, Sort sort);

    @Query("select i from Item i where i.available = true AND (upper(i.name) like upper(concat('%', ?1, '%')) OR " +
            "upper (i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

}
