package kn.testtask.ewallet.rest;

import kn.testtask.ewallet.domain.Owner;
import kn.testtask.ewallet.repository.OwnerRepository;
import kn.testtask.ewallet.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/owner")
public class OwnerRestController {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerService ownerService;

    @GetMapping(value = "/list")
    public List<Owner> getAllOwners() {
        List<Owner> owners = ownerRepository.findAll();
        return owners;
    }

    @GetMapping(value = "/{ownerId}")
    public Owner getOwnerById(@PathVariable Long ownerId) {
        return ownerRepository.findById(ownerId).orElse(null);
    }

    @PostMapping(value = "/add")
    public ResponseEntity addOwner(@RequestBody Owner owner) {
        owner = ownerService.addOwner(owner);

        return new ResponseEntity<Object>(owner, owner != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{ownerId}")
    public ResponseEntity deleteOwner(@PathVariable Long ownerId) {
        boolean result = ownerService.deleteOwner(ownerId);
        return new ResponseEntity<Object>(null, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
