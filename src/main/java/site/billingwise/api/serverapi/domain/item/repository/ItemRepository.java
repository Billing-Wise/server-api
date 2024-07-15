package site.billingwise.api.serverapi.domain.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.user.Client;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
	Optional<Item> findById(Long id);

	List<Item> findByClientAndIdIn(Client client, List<Long> idList);

	Page<Item> findAllByClientId(Pageable pageable, Long clientId);

	Page<Item> findAllByNameContainingIgnoreCaseAndClientId(String itemName, Pageable pageable, Long clientId);

	List<Item> findAllByClientIdAndIsBasic(Long clientId, Boolean isBasic);
}
