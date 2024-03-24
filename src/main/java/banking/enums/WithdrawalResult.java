package banking.enums;

public enum WithdrawalResult {
    ERR_NEGATIVE_AMOUNT("Cannot withdraw a negative amount"),
    ERR_ZERO_AMOUNT("No amount specified for withdrawal"),
    ERR_INSUFFICIENT_FUNDS("Insufficient funds for withdrawal"),
    ERR_UNKNOWN_ACCOUNT("No such account"),
    ERR_UNKNOWN("Unknown error"),
    SUCCESS("Withdrawal successful");

    private final String description;

    WithdrawalResult(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
