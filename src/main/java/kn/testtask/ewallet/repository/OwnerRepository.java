package kn.testtask.ewallet.repository;

import kn.testtask.ewallet.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
