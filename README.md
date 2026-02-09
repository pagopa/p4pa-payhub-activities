# p4pa-payhub-activities

This is a library, written using Spring Boot,  of [Temporal.io](https://temporal.io/) activities, which represent the building blocks of the workflow implemented for **Piattaforma Unitaria** product.

Temporal.io activities are simple Spring bean, which could be used also outside Temporal.io to implement the business logic without this tool.

## 🧱 Role

* To provide a common utility of re-usable activities to use in order to implement workflows:
  * See package `it.gov.pagopa.payhub.activities.activity`
    * It will provide the interfaces useful to implement workflow ([p4pa-workflow-hub](https://github.com/pagopa/p4pa-workflow-hub));
    * It will provide activities' implementation to register to Temporal.io inside the Activity Worker ([p4pa-workflow-worker](https://github.com/pagopa/p4pa-workflow-worker));
  * See [Workflow Confluence page](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1287356459/Workflow) for implemented Workflows.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* Shared folder
* Mail server

### 🧩 Microservices
* [p4pa-auth](https://github.com/pagopa/p4pa-auth):
    * To obtain technical access tokens used on Activities in order to invoke inner microservices;
    * To get operator's email;
* [p4pa-classification](https://github.com/pagopa/p4pa-classification):
    * To access to domain data and operations;
* [p4pa-debt-positions](https://github.com/pagopa/p4pa-debt-positions):
    * To access to domain data and operations;
* [p4pa-organization](https://github.com/pagopa/p4pa-organization):
    * To access to domain data and operations;
* [p4pa-process-executions](https://github.com/pagopa/p4pa-process-executions):
    * To access to domain data and operations;
* [p4pa-io-notification](https://github.com/pagopa/p4pa-io-notification):
    * To send messages through PagoPa's IO app;
* [p4pa-send-notification](https://github.com/pagopa/p4pa-send-notification):
    * To send notification through PagoPa's SEND service;
* [p4pa-pagopa-payments](https://github.com/pagopa/p4pa-pagopa-payments):
    * To sync debt positions towards ACA, GPD, GPD PreLoad;
    * To fetch payments reporting;
    * To get payment pdf notices;
* [p4pa-workflow-hub](https://github.com/pagopa/p4pa-workflow-hub):
    * To start debt position synchronization workflow;
    * To workflow status;
    * To wait workflow termination.

## 🔧 Configuration

See [config/application.yml](src/main/resources/config/application.yml) for each configurable property:
* It has been used the config directory in order to avoid the override by the applications which will use the library.

### 📌 Relevant configurations

#### 🔁 Integrations

##### 🗄️ Resources
| ENV                         | DESCRIPTION                                           | DEFAULT                          |
|-----------------------------|-------------------------------------------------------|----------------------------------|
| SHARED_FOLDER_ROOT          | Absolute path towards shared folder on file system    | /shared                          |
| TMP_FOLDER                  | Absolute path towards temporary folder on file system | /tmp                             |
| MAIL_HOST                   | Mail server host                                      |                                  |
| MAIL_PORT                   | Mail server port                                      | 587                              |
| MAIL_USERNAME               | Mail server username                                  |                                  |
| MAIL_PASSWORD               | Mail server password                                  |                                  |
| MAIL_SMTP_AUTH              | To use authentication                                 | true                             |
| MAIL_SMTP_STARTTLS          | To use TLS protocol if supported by the Mail server   | true                             |
| MAIL_SMTP_STARTTLS_REQUIRED | To require TLS protocol                               | true                             |
| MAIL_SENDER_ADDRESS         | Mail address used as sender                           | piattaforma-unitaria@noreply.com |

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                      | DESCRIPTION                                         | DEFAULT |
|------------------------------------------|-----------------------------------------------------|---------|
| AUTH_BASE_URL                            | Auth microservice URL                               |         |
| AUTH_MAX_ATTEMPTS                        | Auth API max attempts                               | 3       |
| AUTH_WAIT_TIME_MILLIS                    | Auth retry waiting time (milliseconds)              | 500     |
| AUTH_PRINT_BODY_WHEN_ERROR               | To print body when an error occurs                  | true    |
| ORGANIZATION_BASE_URL                    | Organization microservice URL                       |         |
| ORGANIZATION_MAX_ATTEMPTS                | Organization API max attempts                       | 3       |
| ORGANIZATION_WAIT_TIME_MILLIS            | Organization retry waiting time (milliseconds)      | 500     |
| ORGANIZATION_PRINT_BODY_WHEN_ERROR       | To print body when an error occurs                  | true    |
| DEBT_POSITION_BASE_URL                   | DebtPosition microservice URL                       |         |
| DEBT_POSITION_MAX_ATTEMPTS               | DebtPosition API max attempts                       | 3       |
| DEBT_POSITION_WAIT_TIME_MILLIS           | DebtPosition retry waiting time (milliseconds)      | 500     |
| DEBT_POSITION_PRINT_BODY_WHEN_ERROR      | To print body when an error occurs                  | true    |
| PROCESS_EXECUTIONS_BASE_URL              | ProcessExecutions microservice URL                  |         |
| PROCESS_EXECUTIONS_MAX_ATTEMPTS          | ProcessExecutions API max attempts                  | 3       |
| PROCESS_EXECUTIONS_WAIT_TIME_MILLIS      | ProcessExecutions retry waiting time (milliseconds) | 500     |
| PROCESS_EXECUTIONS_PRINT_BODY_WHEN_ERROR | To print body when an error occurs                  | true    |
| CLASSIFICATION_BASE_URL                  | Classification microservice URL                     |         |
| CLASSIFICATION_MAX_ATTEMPTS              | Classification API max attempts                     | 3       |
| CLASSIFICATION_WAIT_TIME_MILLIS          | Classification retry waiting time (milliseconds)    | 500     |
| CLASSIFICATION_PRINT_BODY_WHEN_ERROR     | To print body when an error occurs                  | true    |
| PAGOPA_PAYMENTS_BASE_URL                 | PagoPaPayments microservice URL                     |         |
| PAGOPA_PAYMENTS_MAX_ATTEMPTS             | PagoPaPayments API max attempts                     | 3       |
| PAGOPA_PAYMENTS_WAIT_TIME_MILLIS         | PagoPaPayments retry waiting time (milliseconds)    | 500     |
| PAGOPA_PAYMENTS_PRINT_BODY_WHEN_ERROR    | To print body when an error occurs                  | true    |
| IO_NOTIFICATION_BASE_URL                 | IoNotification microservice URL                     |         |
| IO_NOTIFICATION_MAX_ATTEMPTS             | IoNotification API max attempts                     | 3       |
| IO_NOTIFICATION_WAIT_TIME_MILLIS         | IoNotification retry waiting time (milliseconds)    | 500     |
| IO_NOTIFICATION_PRINT_BODY_WHEN_ERROR    | To print body when an error occurs                  | true    |
| SEND_NOTIFICATION_BASE_URL               | SendNotification microservice URL                   |         |
| SEND_NOTIFICATION_MAX_ATTEMPTS           | SendNotification API max attempts                   | 3       |
| SEND_NOTIFICATION_WAIT_TIME_MILLIS       | SendNotification retry waiting time (milliseconds)  | 500     |
| SEND_NOTIFICATION_PRINT_BODY_WHEN_ERROR  | To print body when an error occurs                  | true    |
| WORKFLOW_HUB_BASE_URL                    | WorkflowHub microservice URL                        |         |
| WORKFLOW_HUB_MAX_ATTEMPTS                | WorkflowHub API max attempts                        | 3       |
| WORKFLOW_HUB_WAIT_TIME_MILLIS            | WorkflowHub retry waiting time (milliseconds)       | 500     |
| WORKFLOW_HUB_PRINT_BODY_WHEN_ERROR       | To print body when an error occurs                  | true    |

#### 💼 Business logic

##### File
| ENV                       | DESCRIPTION                               | DEFAULT  |
|---------------------------|-------------------------------------------|----------|
| CSV_SEPARATOR_CHAR        | CSV column separator                      | ;        |
| CSV_QUOTE_CHAR            | CSV quote character                       | "        |
| ZIP_MAX_ENTRIES           | Maximum allowed number of zip entries     | 1000     |
| ZIP_MAX_UNCOMPRESSED_SIZE | Maximum uncompressed size of zipped files | 52428800 |
| ZIP_MAX_COMPRESSION_RATIO | Maximum compression ratio of zipped files | 100      |

##### ExportFile
| ENV                                             | DESCRIPTION                                                                                       | DEFAULT |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------|---------|
| EXPORT_FLOW_FILES_PAGE_REQUEST_THRESHOLDS_WARN  | Number of page after which the application will start loggin WARN messages                        | 100     |
| EXPORT_FLOW_FILES_PAGE_REQUEST_THRESHOLDS_ERROR | Number of page after which the application will stop the execution setting ERROR as export status | 1000    |
| PAID_EXPORT_FLOW_FILE_SIZE_PAGE                 | Page size configured when exporting paid installments                                             | 10000   |
| RECEIPTS_ARCHIVING_EXPORT_FLOW_FILE_SIZE_PAGE   | Page size configured when exporting receipt for archiving purposes                                | 10000   |
| CLASSIFICATIONS_EXPORT_FLOW_FILE_SIZE_PAGE      | Page size configured when exporting classifications                                               | 10000   |

##### IngestFile
| ENV                                                                       | DESCRIPTION                                                                           | DEFAULT                                                        |
|---------------------------------------------------------------------------|---------------------------------------------------------------------------------------|----------------------------------------------------------------|
| INGEST_FLOW_FILES_PAYMENTS_REPORTING_LEGACY_FEATURE_FLAGS                 | Select between legacy SOAP or new REST PaymentsReportingPagoPaService implementation  | false                                                          |
| INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS               | Default maximum number of rows to process concurrently                                | 10                                                             |
| INGESTION_FLOW_FILES_ASSESSMENTS_MAX_CONCURRENT_PROCESSING_ROWS           | Maximum number of rows to process concurrently during ASSESSMENTS ingestion           | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_ASSESSMENTS_REGISTRY_MAX_CONCURRENT_PROCESSING_ROWS  | Maximum number of rows to process concurrently during ASSESSMENTS_REGISTRY ingestion  | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_DP_INSTALLMENTS_MAX_CONCURRENT_PROCESSING_ROWS       | Maximum number of rows to process concurrently during DP_INSTALLMENTS ingestion       | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_DP_TYPE_MAX_CONCURRENT_PROCESSING_ROWS               | Maximum number of rows to process concurrently during DP_TYPE ingestion               | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_DP_TYPE_ORG_MAX_CONCURRENT_PROCESSING_ROWS           | Maximum number of rows to process concurrently during DP_TYPE_ORG ingestion           | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_ORGANIZATIONS_MAX_CONCURRENT_PROCESSING_ROWS         | Maximum number of rows to process concurrently during ORGANIZATIONS ingestion         | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_ORG_SIL_SERVICES_MAX_CONCURRENT_PROCESSING_ROWS      | Maximum number of rows to process concurrently during ORG_SIL_SERVICES ingestion      | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_PAYMENT_NOTIFICATIONS_MAX_CONCURRENT_PROCESSING_ROWS | Maximum number of rows to process concurrently during PAYMENT_NOTIFICATIONS ingestion | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_RECEIPTS_MAX_CONCURRENT_PROCESSING_ROWS              | Maximum number of rows to process concurrently during RECEIPTS ingestion              | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |
| INGESTION_FLOW_FILES_SEND_NOTIFICATIONS_MAX_CONCURRENT_PROCESSING_ROWS    | Maximum number of rows to process concurrently during SEND_NOTIFICATIONS ingestion    | ${INGESTION_FLOW_FILES_DEFAULT_MAX_CONCURRENT_PROCESSING_ROWS} |

#### 🔑 keys
| ENV                   | DESCRIPTION                                                              | DEFAULT |
|-----------------------|--------------------------------------------------------------------------|---------|
| FILE_ENCRYPT_PASSWORD | Base64 encoded key (256 bit) used to encrypt/decrypt file                |         |
| AUTH_CLIENT_SECRET    | client_secret used on M2M authentication to get a technical access token |         |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### ⚖️ Generate dependencies licenses
```sh
./gradlew generateLicenseReport
```