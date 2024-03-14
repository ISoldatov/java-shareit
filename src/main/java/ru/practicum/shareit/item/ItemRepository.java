package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerIdOrderByIdAsc(int ownerId);

    @Query("SELECT i FROM Item i WHERE i.available=true " +
            "                      AND (lower(i.name) LIKE lower(concat('%',?1,'%')) " +
            "                        OR lower(i.description) LIKE lower(concat('%',?1,'%')))")
    List<Item> findByText(String text);

}
