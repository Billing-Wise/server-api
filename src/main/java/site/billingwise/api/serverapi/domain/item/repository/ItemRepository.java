package site.billingwise.api.serverapi.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.billingwise.api.serverapi.domain.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
  
}
