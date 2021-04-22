package kn.testtask.ewallet.domain;

public enum OperationType {
    DEPOSIT(1),
    WITHDRAW(-1);

    private final int operationSign;

    OperationType(int operationSign) {
        this.operationSign = operationSign;
    }

    public int getOperationSign() {
        return operationSign;
    }
}
