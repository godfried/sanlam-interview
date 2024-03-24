# Bank Account Withdrawals

## Implemented Changes

These are a list of changes that have been implemented to some degree within the project.
The changes chosen were either those that would provide immediate benefit to developers by making the codebase easier to develop, maintain, and test; or easy improvements to potential future pitfalls.
Other improvements were considered but not implemented due to time constraints. See the section *Unimplemented Changes*.

### Usability & Correctness

#### Version Control

An obvious first step is to allow for collaboration, release management, and change tracking. Additionally, standards for
commits, git hooks, and git workflows can be implemented to ensure that code is collaborated on in a consistent manner.

#### Dependency Management

Gradle has been chosen as a tool for dependency management.
This ensures that dependencies are tracked consistently by all users of the project and that users have a repeatable method
for building and executing the application.

#### Modularisation

The code has been refactored as follows:

1. The controller is in the `controller` package.
2. Business logic (account withdrawals and SNS notifications) has been extracted into the `services` package.
3. Repositories live in the `repository` package.
4. The `WithdrawalEvent` has been moved to the data transfer object (`dto`) package.
5. The Account model lives in the `model` package.
6. Code for loading application configuration (e.g. AWS) lives in the `config` package.

This makes the codebase easier to extend, understand, and test.

#### Dependency Injection

Dependency injection has been used to instantiate various services, repositories, and clients. This allows us to decouple
components within the application and makes it more modular and portable.
For example, we can now easily configure different implementations for our components based on the environment.

#### Externalize AWS Configuration

The AWS configuration was moved to a properties file. While this is only the first step, it can be extended so that
the application can be deployed in multiple different environments, accounts, or regions without modifying the code.

#### Abstract Database Queries

The raw database queries were removed and an ORM framework (Jakarta) was used instead.
This improves portability as the application is no longer tightly coupled to a specific SQL dialect.
Additionally, there may also be benefits in developer productivity when using an ORM.

#### Input Validation

Currently, the only validation performed on the input is to ensure that the amount being withdrawn is less than the
available balance. However, there are other inputs that can present problems, for example, negative amounts.
This has been handled appropriately in `SnsService.java`.

#### Logging

Structured logging has been added in the form of log4j2. Unfortunately, this is not working at the moment.

#### Error Handling

Some of the error handling used is insufficient or incorrect. For example, requesting a withdrawal for an account which
does not exist, will result in the incorrect error message "Insufficient funds for withdrawal".
Furthermore, no error handling is performed on the SNS publish step, and the response is not checked or logged.
Lastly, the API returns 200 even when the requests are malformed.
All of these points have been addressed to some degree.

#### Timeouts

Timeouts for the SNS API have been configured as per AWS
recommendations [here](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/best-practices.html#bestpractice5).
The values are just placeholders and should be adjusted with experience.

### Performance

#### Async Calls

An immediately obvious bottleneck in the current implementation is the publishing of events to SNS. If the latency of
these calls are high, we will see our response times suffer.
This improvement is implemented by using an asynchronous client instead (`SnsAsyncClient`) in `SnsClientAdapter.java`
and `SnsService.java`.

#### Batching SNS Calls

Another improvement to the throughput of calls to SNS is to batch the publishing of events to it. This can be
achieved by using the `publishBatch` method instead of `publish`.
This method allows us to publish up to 10 events with a single request. This has been implemented in `SnsService.java`.

#### OpenSSL for AWS Client

As per the [AWS best practices](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/best-practices.html),
using OpenSSL for the SSL provider has been shown to deliver better performance. This has been implemented
in `SnsClientAdapter.java`.

#### JSON Serialization/Deserialization

Using a JSON library (e.g. jackson or fastjson) to serialize the WithdrawalEvents should provide some performance
benefits, especially if the events grow larger and more complex. This has been implemented in `SnsService.java`.

## Unimplemented Changes

### Performance

#### Caching

Currently, querying for the account is done by hitting the database directly. This is a potential bottleneck since the
reads will probably be one of the most common operations in the controller. It would be wise to implement a caching
layer in front of the DB for these reads.
For example, we can add a Redis instance and query this when we need to find our users by ID. We will need to invalidate
the cache for these users if a withdrawal is performed.

#### Horizontal Scaling

Once the use of the application increases, it will have to be scaled horizontally to deal with the increase in traffic. This
can be achieved using autoscaling within the deployment architecture of your choice (EC2, K8S, serverless).

#### Database Scaling

Once we hit higher traffic volumes, we may find that the database is the bottleneck. In this case, we will need to scale
the database itself. This can be achieved by:

1. Adding more replicas if we find that our workload is read-heavy.
2. Partitioning the data, e.g. by account ID, if we need to scale writes up.

#### Load Balancing

Assuming that we have multiple instances of the BankAccountApplication running, we will need to ensure
that traffic is relatively evenly distributed across these instances. This can be achieved by using something like ELB
if we are deployed on AWS or nginx if not.

#### JSON Serialization/Deserialization

Switching to fastjson for SpringBoot's serialization/deserialization can also be considered since fastjson is quite a
bit faster than jackson (the default SpringBoot library).

#### Alternative Data Format

This API will most likely be used for data exchange between different software services and therefore using a data
serialization format with a strict, published API and schema would be more desirable than using JSON.
For example, we could switch to using Protocol Buffers, Cap'n Proto, or Flatbuffers. This also has the advantage of
smaller payloads and faster serialization/deserialization.

### Robustness

#### Degradation

The system should be designed such that it can degrade functionality gracefully in the event of system failures or
resource constraints.
For example, if there is an SNS outage, a fallback can be implemented to write the events to an in-house service which
can then flush to SNS once it is back up.

#### Chaos Testing

Introduce testing where hard faults are injected into the system which simulates real-world scenarios.
e.g. Network outages, power failures, service latency.

#### Automated Recovery

Currently, there is no automated recovery of the system if it should experience a fault.
This can be addressed by implementing automated failovers, rollbacks, and auto-scaling.

#### Recovery Playbooks

Implement playbooks that detail what steps should be taken to recover the system in various failure scenarios.
Continuously review and update these playbooks.

### Usability & Correctness

#### CI/CD

Use CI/CD pipelines to continuously build, test, and deploy our software. This improves bug detection and resolution,
productivity and consistency.

#### Linting

A good step to make the codebase more maintainable is to have a linter execute on every merge request. This ensures that
a specific standard is adhered to within the codebase and can catch unforeseen bugs.

#### Vulnerability Checking

Use tooling to check for outdated, vulnerable dependencies (e.g. OWASP).

#### Testing

A comprehensive test suite will go a long way to improve the correctness of the code.
The API spec should be fully tested, as well as edge cases and error conditions.

#### Prevent Duplicate Transactions

The current implementation can result in the same withdrawal request being processed multiple times if there is a
client-side error.
By using a transaction or request ID to identify duplicate requests, we can prevent this from occurring.

#### Documentation

Currently, documentation is severely lacking in the project. Both from a developer's perspective and for users of the
API.
This will need to be addressed if the project is going to scale beyond one or two developers on the one hand, and have
any users on the other.
For API documentation, something like OpenAPI can be used.

### Monitoring

#### Distributed Tracing

Distributed tracing provides visibility into a distributed system and would allow us to identify bottlenecks and
troubleshoot the system as it grows more complex.

#### Health Checks & Alerting

1. Status page exposing the health of dependencies, resources, and subsystems (externally hosted).
2. Automated alerting mechanisms to notify operators of failures or anomalies in the system.

#### Metrics & Dashboards

Adding metrics to the deployment will be crucial at a later stage to understand how the system performs and be able to
optimise it accordingly. This can be achieved with Prometheus & Grafana for example.

#### Centralised Logging

Using a centralised logging platform (ELK stack for example) will enable us to debug and understand the system as it
scales to many nodes.

#### Runtime Diagnostics

The ability to inspect the internal state of the system at runtime can be essential for diagnosing what is the cause of
poor performance or system failures.
Some possible ways to achieve this are profilers, memory analyzers, and debuggers.

### Auditability

#### Audit Logging

In the banking world, comprehensive audit logging is essential. All critical events, changes, and actions in the system
should be recorded. For example, in our case, all withdrawal attempts and their outcomes should be logged.
Fine-grained details should be included in the logs such as IP addresses, user IDs, and timestamps.

### Cost Efficiency

#### Serverless & Auto Scaling

Using a serverless architecture such as AWS Lambda can produce cost savings by automatically reducing resource usage
during periods of low activity.
This could benefit us if, for example, our users tend to cluster bank withdrawals around a certain period of the day or
week.

#### Managed Services

Use managed services where possible for infrastructure components (e.g. RDS) as they offer built-in cost optimisation
and scaling features.
They also reduce the overhead of managing resources which allows more company resources to be spent elsewhere.

#### Storage Lifecycle

Automatically move older data to cheaper storage tiers (for example, our audit logs).
