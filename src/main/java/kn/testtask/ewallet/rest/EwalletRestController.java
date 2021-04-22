package kn.testtask.ewallet.rest;

import io.swagger.annotations.ApiOperation;
import kn.testtask.ewallet.domain.Ewallet;
import kn.testtask.ewallet.domain.Operation;
import kn.testtask.ewallet.domain.OperationType;
import kn.testtask.ewallet.repository.EwalletRepository;
import kn.testtask.ewallet.service.EwalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/ewallet")
public class EwalletRestController {

    @Autowired
    private EwalletService ewalletService;

    @Autowired
    private EwalletRepository ewalletRepository;

    @GetMapping(value = "/list")
    public List<Ewallet> getAllEwalletsOrderedByOwnerAndCurrencyAndAmount() {
        return ewalletRepository.findAll(Sort.by(Sort.Direction.ASC, "owner", "currency", "amount"));
    }

    @GetMapping(value = "/list/{ownerId}")
    public List<Ewallet> getAllEwalletsByOwnerId(@PathVariable Long ownerId) {
        return ewalletRepository.findByOwner(ownerId);
    }

    @GetMapping(value = "/balance/{ewalletId}")
    public BigDecimal getBalanceOfEwalletById(@PathVariable Long ewalletId) {
        Ewallet ewallet = ewalletRepository.findById(ewalletId).orElse(null);
        if (ewallet == null) {
            return BigDecimal.ZERO;
        }
        return ewallet.getAmount();
    }

    @PutMapping(value = "deposit")
    public ResponseEntity<Ewallet> depositMoneyToEwallet(@RequestBody Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Don't be afraid. It is for further processing with @RestControllerAdvice...");
        }
        operation.setType(OperationType.DEPOSIT);
        Ewallet ewallet = ewalletService.processEwalletOperation(operation);
        if (ewallet == null) {
            throw new IllegalArgumentException("Don't be afraid. It is for further processing with @RestControllerAdvice...");
        }

        return new ResponseEntity<Ewallet>(ewallet, HttpStatus.OK);
    }

    @PutMapping(value = "withdraw")
    public ResponseEntity withdrawMoneyFromEwallet(@RequestBody Operation operation) {
        if (operation == null) {
            throw new IllegalArgumentException("Don't be afraid. It is for further processing with @RestControllerAdvice...");
        }
        operation.setType(OperationType.WITHDRAW);
        Ewallet ewallet = ewalletService.processEwalletOperation(operation);
        Map<String,String> responseBody = new HashMap<>();
        if (ewallet == null) {
            throw new IllegalArgumentException("Don't be afraid. It is for further processing with @RestControllerAdvice...");
        }

        return new ResponseEntity<Ewallet>(ewallet, HttpStatus.OK);
    }

    @PutMapping(value = "transfer")
    @ApiOperation(value = "Transfer money between two e-wallets",
            notes = "Provide Operation[] with 2 elements. \nFirst element defines wallet to withdraw: ownerId, ewalletId, amount must be filled." +
            " \nSecond element defines e-wallet to deposit: ownerId, ewalletId must be filled.",
            consumes = "Operation[]")
    public ResponseEntity withdrawMoneyFromEwallet(@RequestBody Operation[] operations) {
        boolean result = operations.length == 2;
        if (result) {
            operations[0].setType(OperationType.WITHDRAW);
            operations[1].setType(OperationType.DEPOSIT);
            operations[1].setAmount(operations[0].getAmount());
            result = ewalletService.processEwalletTransfer(operations);
        }

        Map<String,String> responseBody = new HashMap<>();
        if (!result) {
            responseBody.put("message","Wrong parameters of transfer.");
        }

        return new ResponseEntity<Object>(responseBody, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "Add e-wallet for owner",
    notes = "Provide Ewallet object with filled: name, currency, owner. Id must be null.",
    consumes = "Ewallet object with filled: name, currency, owner. Id must be null.")
    public ResponseEntity addEwalletToOwner(@RequestBody Ewallet ewallet) {
        ewallet = ewalletService.addEwallet(ewallet);
        return new ResponseEntity(ewallet, ewallet != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete/{ewalletId}")
    public ResponseEntity deleteEwallet(@PathVariable Long ewalletId) {
        boolean result = ewalletService.deleteEwallet(ewalletId);
        return new ResponseEntity<Object>(null, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
