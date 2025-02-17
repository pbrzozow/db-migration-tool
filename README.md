# Migration DB Tool

This project provides a comprehensive database version control utility. It can be used both as a command-line interface (CLI) application and as a standard Java library integrated into your application.

## Deployment

To deploy the database for the project, navigate to the repository directory and execute the following command:

```
docker-compose up -d
```

### CLI Configuration

If you intend to use the application as a CLI tool, you need to create a `config.properties` file in the directory containing the application's JAR file. Within this file, define the path to the directory that contains the `changelog/` and `rollback/` folders.

```
migration.dir = C:\YourFilePath
```


Ensure the specified directory contains the necessary migration and rollback scripts before executing commands.

## Features

### 1. Database Migrations
Migrations allow you to apply changes to your database structure by placing SQL migration files in the `changelog/` directory.

#### File Naming Convention:
Each migration file should follow the naming pattern:
```
V{number}__description.sql
```

#### Executing a Migration
To execute a migration via CLI, use the following command:

```
java -jar filename.jar migrate
```

To execute a migration within a Java application:
```
manager.migrate();
```
### 2. Rollbacks
Rollbacks allow you to revert changes made to the database by providing SQL rollback scripts in the `rollback/` directory.

#### File Naming Convention:
Each rollback file should be named using the following pattern:
```
U{number}__description.sql
```
#### Executing a Rollback
To perform a rollback using CLI:
```
java -jar filename.jar rollback <id>
```
Where <id> corresponds to the migration number you want to undo.

To execute a rollback within a Java application:
```
manager.rollback(id);
```
### 3. History
The tool keeps a record of all database migrations applied. You can retrieve the history of changes to monitor database modifications over time.

#### Viewing Migration History
To view the migration history via CLI:
```
java -jar filename.jar history
```
To retrieve the migration history within a Java application:
```
manager.showHistory();
```



