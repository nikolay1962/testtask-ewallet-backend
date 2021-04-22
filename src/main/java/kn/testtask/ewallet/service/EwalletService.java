package kn.testtask.ewallet.service;

import kn.testtask.ewallet.domain.Ewallet;
import kn.testtask.ewallet.domain.Operation;
import kn.testtask.ewallet.repository.EwalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EwalletService {

    private final EwalletRepository ewalletRepository;

    private final OwnerService ownerService;

    public Ewallet processEwalletOperation(Operation operation) {
        if (isOperationPossible(operation)) {
            return updateEwallet(operation);
        }
        return null;
    }

    private boolean hasEwalletEnoughFunds(Operation operation, Ewallet ewallet) {

        if (operation.getType().getOperationSign() < 0) {
            return ewallet.getAmount().compareTo(operation.getAmount()) >= 0;
        }
        return true;
    }

    private Ewallet updateEwallet(Operation operation) {
        Ewallet ewallet = ewalletRepository.findById(operation.getEwalletId()).orElse(null);
        if (operation.getType().getOperationSign() > 0) {
            ewallet.setAmount(ewallet.getAmount().add(operation.getAmount()));
        }
        if (operation.getType().getOperationSign() < 0) {
            ewallet.setAmount(ewallet.getAmount().subtract(operation.getAmount()));
        }
        return ewalletRepository.saveAndFlush(ewallet);
    }

    public boolean processEwalletTransfer(Operation[] operations) {
        if (operations == null
                || operations.length != 2
                || operations[0] == null
                || operations[1] == null) {
            return false;
        }
        if (isOperationPossible(operations[0])
                && isOperationPossible(operations[1])
                && isTransferPossible(operations)) {
            updateEwallet(operations[0]);
            updateEwallet(operations[1]);
            return true;
        }
        return false;
    }

    private boolean isTransferPossible(Operation[] operations) {
        // check if it is not the same wallet
        boolean result = !operations[0].getEwalletId().equals(operations[1].getEwalletId());
        if (result) {
            // check if currencies are equal
            String currency = getCurrencyOfOperation(operations[0]);
            if (currency == null) {
                return false;
            }
            return currency.equals(getCurrencyOfOperation(operations[1]));
        }
        return result;
    }

    private boolean isEwalletOwnerCorrect(Operation operation, Ewallet ewallet) {
        if (operation == null || operation.getOwnerId() == null || ewallet == null) {
            return false;
        }

        return operation.getOwnerId().equals(ewallet.getOwner());
    }

    private boolean isOperationAmountCorrect(Operation operation) {
        if (operation == null
                || operation.getAmount() == null
                || operation.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return true;
    }

    private boolean isOperationPossible(Operation operation) {
        if (operation == null || operation.getEwalletId() == null) {
            return false;
        }
        Ewallet ewallet = ewalletRepository.findById(operation.getEwalletId()).orElse(null);

        if (ewallet == null
                || !isEwalletOwnerCorrect(operation, ewallet)
                || !isOperationAmountCorrect(operation)
                || !hasEwalletEnoughFunds(operation, ewallet)) {
            return false;
        }
        return true;
    }

    private String getCurrencyOfOperation(Operation operation) {
        Ewallet ewallet = ewalletRepository.findById(operation.getEwalletId()).orElse(null);
        if (ewallet == null) {
            return null;
        }
        return ewallet.getCurrency();
    }

    public Ewallet addEwallet(Ewallet ewallet) {
        if (ewallet == null
                || ewallet.getId() != null
                || ewallet.getCurrency() == null
                || ewallet.getName() == null
                || !ownerService.existsOwner(ewallet.getOwner())) {
            return null;
        }
        // set amount to zero for a new wallet
        ewallet.setAmount(BigDecimal.ZERO);
        try {
            ewallet = ewalletRepository.saveAndFlush(ewallet);
        } catch (Exception ex) {
            ewallet = null;
        }
        return ewallet;
    }

    public boolean deleteEwallet(Long ewalletId) {
        if (canEwalletBeDeleted(ewalletId)) {
            ewalletRepository.deleteById(ewalletId);
            return true;
        }
        return false;
    }

    private boolean canEwalletBeDeleted(Long ewalletId) {
        if (ewalletId == null) {
            return false;
        }
        Ewallet ewallet = ewalletRepository.findById(ewalletId).orElse(null);
        return ewallet != null && ewallet.getAmount().compareTo(BigDecimal.ZERO) == 0;
    }
}
