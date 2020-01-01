# Overview

# Getting Started
NACOS does not work currently.

## Install google-java-format plugin in IntelliJ
Follow the instructions on ``https://github.com/google/google-java-format``

The import ordering is not handled by this plugin, unfortunately. To fix the import order, download the IntelliJ Java Google Style file and import it into File→Settings→Editor→Code Style.
``https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml``

# Structure

| Module | Purpose |
| -------- | -------- |
|`oms-db` | DB Access |
|`oms-common` | Shared business implementation |
|`oms-rest` | REST API |
|`oms-graphql` | GraphQL API |

# Pre-Requisities
* Docker for Mac
* JDK 1.8
* PostgreSQL 10.0

# Building
Use Gradle wrapper 
```
./gradlew clean build
```

# Running Services

## Start background services
```
```
## DB Foreign Data Wrapper
```
```