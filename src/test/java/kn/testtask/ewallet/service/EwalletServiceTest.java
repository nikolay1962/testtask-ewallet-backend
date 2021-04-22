package kn.testtask.ewallet.service;

import kn.testtask.ewallet.domain.Ewallet;
import kn.testtask.ewallet.domain.Operation;
import kn.testtask.ewallet.domain.OperationType;
import kn.testtask.ewallet.repository.EwalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EwalletServiceTest {

    private EwalletService ewalletService;

    @Mock
    private EwalletRepository ewalletRepository;

    @Mock
    private OwnerService ownerService;

    @BeforeEach
    public void setUp() {
        this.ewalletRepository = Mockito.mock(EwalletRepository.class);
        this.ownerService = Mockito.mock(OwnerService.class);
        this.ewalletService = new EwalletService(this.ewalletRepository, this.ownerService);
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsNullIfOperationIsNull() {
        assertNull(this.ewalletService.processEwalletOperation(null));
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsNullIfEwalletIdIsNull() {
        Operation operation = new Operation();
        assertNull(this.ewalletService.processEwalletOperation(operation));
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsNullIfEwalletIsNull() {
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(new Ewallet()));
        assertNull(this.ewalletService.processEwalletOperation(operation));
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsNullIfOwnersAreDifferent() {
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(2l);
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(ewallet));
        assertNull(this.ewalletService.processEwalletOperation(operation));
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsEwalletIfCorrectDeposit() {
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        operation.setType(OperationType.DEPOSIT);
        operation.setAmount(BigDecimal.ONE);
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(1l);
        ewallet.setId(1l);
        ewallet.setAmount(BigDecimal.ZERO);
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(ewallet));
        Mockito.when(ewalletRepository.saveAndFlush(ewallet)).thenReturn(ewallet);
        Ewallet ewallet1 = this.ewalletService.processEwalletOperation(operation);
        assertEquals(this.ewalletService.processEwalletOperation(operation).getOwner(), 1l);
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsEwalletIfCorrectWithdraw() {
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        operation.setType(OperationType.WITHDRAW);
        operation.setAmount(BigDecimal.ONE);
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(1l);
        ewallet.setAmount(BigDecimal.ONE);
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(ewallet));
        Mockito.when(ewalletRepository.saveAndFlush(ewallet)).thenReturn(ewallet);
        assertEquals(this.ewalletService.processEwalletOperation(operation).getOwner(), 1l);
    }

    @Test
    void testThat_ProcessEwalletOperationReturnsNullIfIncorrectDeposit() {
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        operation.setType(OperationType.WITHDRAW);
        operation.setAmount(BigDecimal.ONE);
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(1l);
        ewallet.setAmount(BigDecimal.ZERO);
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(ewallet));
        assertNull(this.ewalletService.processEwalletOperation(operation));
    }

    @Test
    void testThat_processEwalletTransferReturnsFalseIfOperationsIsNull() {
        assertFalse(this.ewalletService.processEwalletTransfer(null));
    }

    @Test
    void testThat_processEwalletTransferReturnsFalseIfOperationElementsAreNull() {
        Operation[] operations = new Operation[2];
        assertFalse(this.ewalletService.processEwalletTransfer(operations));
    }

    @Test
    void testThat_processEwalletTransferReturnsFalseIfOperationsLengthIsNot2() {
        Operation[] operations = new Operation[3];
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        operation.setType(OperationType.WITHDRAW);
        operation.setAmount(BigDecimal.ONE);

        operations[0] = operation;
        operations[1] = operation;
        assertFalse(this.ewalletService.processEwalletTransfer(operations));
    }

    @Test
    void testThat_processEwalletTransferReturnsFalseIfOperationsForTheSameEwallet() {
        Operation[] operations = new Operation[3];
        Operation operation = new Operation();
        operation.setEwalletId(1l);
        operation.setOwnerId(1l);
        operation.setType(OperationType.WITHDRAW);
        operation.setAmount(BigDecimal.ONE);
        operations[0] = operation;
        operations[1] = operation;
        assertFalse(this.ewalletService.processEwalletTransfer(operations));
    }

    @Test
    void testThat_processEwalletTransferReturnsTrueIfCorrectOperations() {
        Operation[] operations = new Operation[3];
        Operation operationW = new Operation();
        operationW.setEwalletId(1l);
        operationW.setOwnerId(1l);
        operationW.setType(OperationType.WITHDRAW);
        operationW.setAmount(BigDecimal.ONE);

        Operation operationD = new Operation();
        operationD.setEwalletId(2l);
        operationD.setOwnerId(2l);
        operationD.setType(OperationType.DEPOSIT);
        operationD.setAmount(BigDecimal.ONE);

        operations[0] = operationW;
        operations[1] = operationD;
        Ewallet ewalletW = new Ewallet();
        ewalletW.setOwner(1l);
        ewalletW.setAmount(BigDecimal.ONE);
        ewalletW.setCurrency("EUR");
        Mockito.when(ewalletRepository.findById(1l)).thenReturn(java.util.Optional.of(ewalletW));
        Ewallet ewalletD = new Ewallet();
        ewalletD.setOwner(2l);
        ewalletD.setAmount(BigDecimal.ZERO);
        ewalletD.setCurrency("EUR");
        Mockito.when(ewalletRepository.findById(2l)).thenReturn(java.util.Optional.of(ewalletD));
        assertFalse(this.ewalletService.processEwalletTransfer(operations));
    }

    @Test
    void testThat_addEwalletReturnsNullIfEwallettIsNull() {
        assertNull(this.ewalletService.addEwallet(null));
    }

    @Test
    void testThat_addEwalletReturnsNullIfOwnerDoesNotExists() {
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(1l);
        ewallet.setAmount(BigDecimal.ONE);
        ewallet.setCurrency("EUR");
        Mockito.when(ownerService.existsOwner(1l)).thenReturn(false);
        assertNull(this.ewalletService.addEwallet(ewallet));
    }

    @Test
    void testThat_addEwalletReturnsEwalletIfCorrectSettings() {
        Ewallet ewallet = new Ewallet();
        ewallet.setOwner(1l);
        ewallet.setAmount(BigDecimal.ONE);
        ewallet.setCurrency("EUR");
        ewallet.setName("Test Ewallet");
        Mockito.when(ownerService.existsOwner(1l)).thenReturn(true);
        Mockito.when(ewalletRepository.save(ewallet)).thenReturn(ewallet);
        Mockito.when(ewalletRepository.saveAndFlush(ewallet)).thenReturn(ewallet);
        assertNotNull(this.ewalletService.addEwallet(ewallet));
        assertEquals(ewallet.getAmount().compareTo(BigDecimal.ZERO), 0);
    }
}