package kn.testtask.ewallet.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Operation {
    private Long ownerId;
    private Long ewalletId;
    private OperationType type;
    private BigDecimal amount;
}
