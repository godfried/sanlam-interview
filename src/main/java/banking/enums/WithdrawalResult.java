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

    public boolean isClientError() {
        return switch (this) {
            case ERR_NEGATIVE_AMOUNT, ERR_ZERO_AMOUNT, ERR_INSUFFICIENT_FUNDS, ERR_UNKNOWN_ACCOUNT -> true;
            default -> false;
        };
    }

    public boolean isServerError() {
        return this.equals(ERR_UNKNOWN);
    }

    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
}
