# Bank Account Withdrawals

NB: for suggested changes and evaluation, see EVALUATION.md

## Requirements

1. Java JDK 20
2. Gradle 8.x (built with 8.4)

## Execution

From project root, run:

```shell
./gradlew bootRun
```

Alternatively, this project was built in Intellij, so it can be imported and executed from there.

Once running, you can use the withdrawal API as follows:

```shell
curl --request POST 'localhost:8080/bank/withdraw?amount=10&accountId=1'
```

The database comes pre-populated with some data, see `data.sql`.