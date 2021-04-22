package kn.testtask.ewallet.repository;

import kn.testtask.ewallet.domain.Ewallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EwalletRepository extends JpaRepository<Ewallet, Long> {
    List<Ewallet> findByOwner(Long ownerId);
}
