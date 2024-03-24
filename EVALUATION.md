# Bank Account Withdrawals

## Implemented Changes

### Caching

Currently, querying for the account is done by hitting the database directly. This is a potential bottleneck since these
type of reads will probably be one of the most common operations in the controller (we assume there are other API calls
besides withdraw). It would be wise to implement a caching layer in front of the DB for these reads.
For example, we can add a Redis instance and query this when we need to find our users by ID. We will need to invalidate
the cache for these users if a withdrawal is performed.

### Async Calls

An immediately obvious bottleneck in the current implementation is the publishing of events to SNS. This can be improved
by using an asynchronous client instead (SnsAsyncClient).

### Batching SNS

Another approach to improve the throughput of calls to SNS, is to batch the publishing of events to it. This can be
achieved by using the `publishBatch` method instead of `publish`.
This method allows us to publish up to 10 events with a single request.

### OpenSSL for AWS Client

As per the [AWS best practices](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/best-practices.html),
using OpenSSL for the SSL provider has been shown to deliver better performance.

### JSON Serialization/Deserialization

Using a JSON library (e.g. jackson or fastjson) to serialize the WithdrawalEvents should provide some performance
benefits, especially if the events grow larger and more complex.

### Version Control

An obvious first step. Additionally, standards for commits, git hooks and git workflows can be implemented to ensure
that code is collaborated on in a consistent manner.

### Dependency Management

Use a tool to manage dependencies (e.g. Maven or Gradle).

### Dependency Injection

Use dependency injection to instantiate the SnsClient & JdbcTemplate.
This allows for easier configuration and swapping them out for alternate implementations based on the environment.

### Externalize AWS Configuration

Move the AWS configuration to a properties file or external configuration manager (etcd).
This ensures that the application can easily be deployed in different environments, accounts or regions without
modifying the code.

### Abstract Database Queries

Switch to using an ORM framework so that the code is not tightly coupled to a specific SQL dialect or database schema.

### Input Validation

Currently, the only validation performed on the input is to ensure that the amount being withdrawn is less than the
available balance.
However, there are other inputs which can present problems, for example, negative amounts.
A full set of validation rules should be created and used to validate the input params. It would be a good idea to
implement a rules engine for this.

## Unimplemented Changes

### Load Balancing

See scalability. Assuming that we have multiple instances of the BankAccountApplication running, we will need to ensure
that traffic is relatively evenly distributed across these instances. This can be achieved by using something like ELB
if we are deployed on AWS or nginx if not.

### JSON Serialization/Deserialization

Additionally, switching to fastjson for SpringBoot's serialization/deserialization can also be considered, since
fastjson is quite a bit faster than jackson (the default SpringBoot library).

### Alternative Data Format

This API will most likely be used for data exchange between different software services and therefore using a data
serialization format with a strict, published API and schema would be more desirable than using JSON.
For example, we could switch to using Protocol Buffers, Cap'n Proto or Flatbuffers. This also has the advantage that of
smaller payloads and faster serialization/deserialization.

### Error Handling

Some of the error handling used is insufficient or incorrect.
For example, requesting a withdrawal for an account which does not exist will result in the incorrect error message "
Insufficient funds for withdrawal".
Furthermore, no error handling is performed on the SNS publish step and the response is not checked or logged.

### Degradation

The system should be designed such that it can degrade functionality gracefully in the event of system failures or
resource constraints.
For example, if there is a SNS outage, a fallback can be implemented to write the events to an in-house service which
can then flush to SNS once it is back up.
Alternatively, writes to SNS can just fail without impacting the rest of the service if this is not a critical path.

### Chaos Testing

Introduce testing where hard faults are injected into the system which simulate real-world scenarios.
e.g. Network outages, power failures, service latency.

### Automated Recovery

Automated failovers, auto rollbacks, auto-scaling.

### Recovery Playbooks

Implement playbooks which detail what steps should be taken to recover the system in various failure scenarios.
Continuously review and update these playbooks.

### Linting

A good step to make the codebase more maintainable is to

### Dependency Checking

Use tooling to check for outdated, vulnerable dependencies (e.g. OWASP),

### Logging

#### Structured Logging

### Instrumentation

### Distributed Tracing

### Health Checks & Alerting

1. Status page exposing the health of dependencies, resources and subsystems (externally hosted).
2. Automated alerting mechanisms to notify operators of failures or anomalies in the system.

### Centralised Logging & Monitoring

### Runtime Diagnostics

The ability to inspect the internal state of the system at runtime can be essential for diagnosing what is the cause of
poor performance or system failures.
Some possible ways to achieve this are: profilers, memory analyzers and debuggers.

## Auditability

### Audit Logging

In the banking world, comprehensive audit logging is essential. All critical events, changes and actions in the system
should be recorded. For example, in our case, all withdrawal attempts and their outcomes should be logged.
Fine-grained details should be included in the logs such as IP addresses, user IDs and timestamps.

### Immutable Log Storage

Audit logs should be stored in a secure and tamper-proof (or at least tamper-evident) manner to ensure their integrity
and authenticity.
Some solutions for this are: append-only log files, write-once storage systems or blockchain-based technology.

### Access Controls

RBAC

### Retention Policies

### Data Masking & Reviewing

### Documentation & Reporting

## Portability

## Correctness

### Testing

A comprehensive test suite will go a long way to improve the correctness of the code.
The API spec should be fully tested, as well as edge cases and error conditions.

### Prevent Duplicate Transactions

The current implementation can result in the same withdrawal request being processed multiple times if there is a
client-side error.
By using a transaction or request ID to identify duplicate requests, we can prevent this from occurring.

## Cost Efficiency

### Resource Usage

Analyse resource usage to ensure that assigned resources are appropriate for the application.
Continuously update and adjust as workloads change.
e.g. Compute resources, databases and storage.

### Serverless/Auto Scaling

Using a serverless architecture such as AWS Lambda can produce cost savings by automatically reducing resource usage
during periods of low activity.
This could benefit us if, for example, our users tend to cluster bank withdrawals around a certain period of the day or
week.

### Managed Services

Use managed services where possible for infrastructure components (RDS, SNS) as they offer built-in cost optimisation
and scaling features.
They also reduce the overhead of managing resources which allows more company resources to be spent elsewhere.

### Storage Lifecycle

Automatically move older data to cheaper storage tiers (for example, our audit logs).

### CI/CD

### Documentation

### Testing

## Scalability

### Horizontal Scaling

### Stateless

### Database Scaling

### Caching

### Asynchronous Processing

### Monitoring

### Autoscaling

### Fault Tolerance

## Flexibility

### Modular Architecture

### Dependency Injection

### Externalise Configuration

### Plugin Architecture

### Feature Flags

### Containerization

### Service-Oriented Architecture

## Consistency

## Fault Tolerance

### Retry Mechanisms

### Circuit Breaker

### Timeouts

### Health Checks & Monitoring

### Redundancy & Replication

### Stateless

## Testability

### Dependency Injection

### TDD

### Inversion of Control

### Single Responsibility Principle

### CI/CD

## Dependency Management

## Observability

### Caching

Implement caching

## Quality

