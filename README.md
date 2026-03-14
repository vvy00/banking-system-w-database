# Java Banking System with MySQL Database

A console-based banking application that demonstrates **Object-Oriented Programming (OOP)** and **Relational Database Management**.

## 🚀 Features
* **Secure Login:** Separate portals for Customers and Administrators.
* **Real-time Transactions:** Deposit and Withdraw funds with instant SQL updates.
* **Admin Dashboard:** Full CRUD capabilities (Create, Read, Update, Delete) for managing bank accounts.
* **Data Persistence:** Uses JDBC to connect to a MySQL database, ensuring data is saved between sessions.

## 🛠️ Tech Stack
* **Language:** Java
* **Database:** MySQL
* **Driver:** MySQL Connector/J

## 📂 Database Schema
To set up the database, run the following SQL commands:

```sql
CREATE DATABASE BankDB;
USE BankDB;

CREATE TABLE accounts (
    id INT PRIMARY KEY,
    pin VARCHAR(20),
    name VARCHAR(100),
    balance DECIMAL(15, 2),
    is_admin BOOLEAN
);
