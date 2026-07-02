# OSGi Error Interpreter Lambda

This project exposes an OSGi error interpreter two ways:

- A `POST /errorInterpreter` HTTP endpoint used by the HTML front-end.
- A `POST /mcp` [Model Context Protocol](https://modelcontextprotocol.io) endpoint that lets LLMs (such as Claude) call the interpreter as a tool when they encounter an OSGi error.

## MCP server

The `/mcp` route is a stateless MCP server that speaks the Streamable HTTP transport using single JSON responses (it does not open server-initiated SSE streams). It exposes one tool:

- `interpret_osgi_error` – input `{ "errorMessage": string }`; returns a plain-text interpretation plus generic and Elastic Path Self-Managed Commerce remediation guidance.

### Connect a client

For Claude Code:

```bash
claude mcp add --transport http osgi-error-interpreter <api-base-url>/mcp
```

Other clients accept the same URL as a remote/HTTP MCP server. No local installation is required.

### Test the MCP endpoint locally

The `samples/mcp-*.json` files contain ready-to-use JSON-RPC requests in API Gateway proxy format:

```bash
sls invoke local --function mcp --path samples/mcp-initialize.json --verbose
sls invoke local --function mcp --path samples/mcp-tools-list.json --verbose
sls invoke local --function mcp --path samples/mcp-tools-call.json --verbose
```

## Build

```bash
mvn clean install
```

## Test Lambda locally

To run the Lambda locally without deploying to AWS, use these steps:

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
