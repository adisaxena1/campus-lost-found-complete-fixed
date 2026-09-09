package com.campus.lostfound.repository;

import com.campus.lostfound.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findAllByOrderByDateDesc();
    List<Item> findByReporterIdOrderByDateDesc(Long reporterId);
    long countByStatus(ItemStatus status);
    long countByType(ItemType type);
}
