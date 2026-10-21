# Courier and Dispatch Management Application

## Overview

The **Courier and Dispatch Management Application** is a Java-based desktop application designed to manage courier operations, including courier registration, storage management, status tracking, and dispatch processing.

The application provides a graphical user interface built with Java Swing and uses a MySQL database hosted on an online server to store and manage application data.

## Features

- **Courier Management:** Register and manage courier records.
- **Courier Status Tracking:** Track courier progress through different statuses.
- **Storage Management:** Manage storage locations for couriers awaiting dispatch.
- **Dispatch Management:** Record dispatch information and update courier statuses.
- **Database Integration:** Store and retrieve application data using MySQL.
- **Graphical User Interface:** Interact with the application through a Java Swing interface.
- **Data Validation:** Handle invalid operations and status transitions using application logic and custom exceptions.

## Technologies Used

- **Java** — application logic and object-oriented programming.
- **Java Swing** — graphical user interface.
- **MySQL** — relational database hosted on an online server.
- **MySQL Connector/J 8.3.0** — JDBC driver for connecting Java to MySQL.
- **SLF4J** — logging framework and logging implementation.
- **JDBC** — database connectivity.

## Project Structure

The project contains the following main components:

- `java files/` — Java source code, including GUI, model, database, and other application classes.
- `libraries/` — external libraries required by the application.
- `compiled files/` — compiled Java class files, if included.
- `run.bat` — Windows batch launcher for running the application.

## Requirements

- Java Runtime Environment (JRE) or Java Development Kit (JDK) compatible with the compiled application.
- Windows operating system to use the supplied `run.bat` launcher.
- Required project libraries.
- Network access to the online MySQL server.
- Valid database connection configuration.

## Running the Application

1. Download or clone this repository.
2. Ensure Java is installed and available on your system.
3. Ensure the required libraries are available.
4. Configure the database connection settings for the online MySQL server.
5. Run `run.bat` on Windows to launch the application.

The database server must be accessible from your network, and the configured database account must have the required permissions.

## Database

The application uses MySQL to persist courier and dispatch information.

Database connection settings include the server host, port, database name, username, and password. These settings must be configured correctly for the application to connect successfully.

**Security:** Database passwords and other sensitive credentials should not be committed to a public repository. Configure credentials securely before running the application.

## Object-Oriented Design

The application uses object-oriented programming concepts, including classes, interfaces, enumerations, and exception handling to organize courier operations and enforce application rules.

## Academic Project

This application was developed as a software project to demonstrate Java programming, GUI development, database integration, and object-oriented software design.


The end
