package kn.testtask.ewallet.service;

import kn.testtask.ewallet.domain.Owner;
import kn.testtask.ewallet.repository.EwalletRepository;
import kn.testtask.ewallet.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class OwnerService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EwalletRepository ewalletRepository;

    public Owner addOwner(Owner owner) {
        if (owner.getName() == null || owner.getEmail() == null || owner.getId() != null) {
            return null;
        }
        try {
            owner = ownerRepository.saveAndFlush(owner);
        } catch (Exception ex) {
            owner = null;
        }

        return owner;
    }

    public boolean existsOwner(Long ownerId) {
        if (ownerId == null) {
            return false;
        }
        Owner owner = ownerRepository.findById(ownerId).orElse(null);
        return owner != null;
    }

    public boolean canOwnerBeDeleted(Owner owner) {
        // check if any wallet still has money
        return !owner.getEwallets().stream().anyMatch(ewallet -> ewallet.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    public boolean deleteOwner(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId).orElse(null);

        if (owner != null && canOwnerBeDeleted(owner)) {
            deleteByOwner(owner);
            ownerRepository.delete(owner);
            return true;
        }
        return false;
    }

    public void deleteByOwner(Owner owner) {
        ewalletRepository.deleteAll(owner.getEwallets());
    }
}
