package banking.model;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public Account() {

    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void withDraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) throw new RuntimeException("Insufficient funds.");
        this.balance = this.balance.subtract(amount);
    }

}

