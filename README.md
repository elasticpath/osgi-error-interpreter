# OSGi Error Interpreter Lambda

## Build

```bash
mvn clean install
```

NOTE: By default, the build only runs unit tests and integration tests. For additional tests, see the following sections.

## Test Lambda locally

To run the lambdas locally without deploying to AWS, use these steps:

1. Install the [Serverless Framework’s Open Source CLI](https://www.serverless.com/framework/docs/getting-started/).
1. Invoke the Lambda functions locally as follows:
   ```bash
   sls invoke local --function errorInterpreter --path samples/sample1.json --verbose
   ```
1. To run the commands with remote debugging enabled, run the following before the `sls invoke local` command:
   ```bash
   export JAVA_TOOL_OPTIONS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"
   ```

## Deploy to AWS

1. Install the [AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html).
2. Run `aws configure` and enter your AWS account details.
3. Deploy the Lambda using `sls deploy`.
