# FinVerse Wallet

FinVerse Wallet is a desktop-based personal finance management application built with Java 21 and JavaFX.
It provides a clean, intuitive graphical interface through which users can register an account, securely log
in, manage their PKR-denominated wallet, deposit and withdraw funds, transfer money to other registered users
by username, and review a full transaction history — all backed by a MySQL relational database and BCrypt-hashed
password storage.

---

## Group Members

| Full Name | CMS / ID | Section |
|-----------|----------|---------|
| M.Subaiyal |023-25-0109 | E |
|Syeda Kashaf Batool |023-25-0052 |E |
| Naveed Akbar |023-25-0087 | E |


## Purpose

The purpose of FinVerse Wallet is to simulate a lightweight digital wallet system that demonstrates core software-engineering concepts: layered architecture (MVC + DAO + Service), relational database design with referential integrity, secure credential management, and event-driven JavaFX UI development. The application targets students and academic evaluation environments, providing a realistic yet self-contained example of a transactional financial system.

---

## Main Modules

```
FinverseWallet/
├── src/main/java/org/example/
│   ├── Main.java                        # Application entry point; bootstraps JavaFX stage
│   ├── MainController.java              # Root/splash controller
│   ├── controllers/
│   │   ├── LoginController.java         # Handles login form events and session init
│   │   ├── RegisterController.java      # Handles new-user registration form
│   │   ├── DashBoardController.java     # Dashboard: balance display, deposit/withdraw, TX history
│   │   └── TransferController.java      # Peer-to-peer transfer by username
│   ├── services/
│   │   ├── AuthService.java             # Registration validation, BCrypt hashing, login logic
│   │   └── WalletService.java           # Deposit, withdraw, transfer, balance retrieval
│   ├── dao/
│   │   ├── UserDAO.java                 # CRUD operations on the User table
│   │   ├── WalletDAO.java               # Wallet creation, balance read/update
│   │   └── TransactionDAO.java          # Transaction logging and history queries
│   ├── models/
│   │   ├── User.java                    # User entity (id, username, email, passwordHash)
│   │   ├── Wallet.java                  # Wallet entity (walletId, userId, balance, currency)
│   │   └── Transaction.java             # Transaction entity (type, amount, status, timestamp)
│   ├── db/
│   │   └── DBConnection.java            # MySQL JDBC singleton connection helper
│   └── utils/
│       ├── SessionManager.java          # In-memory session: stores current User & Wallet
│       └── SceneManager.java            # Utility for switching JavaFX scenes
└── src/main/resources/
    ├── FXML/
    │   ├── login.fxml                   # Login screen layout
    │   ├── Register.fxml                # Registration screen layout
    │   ├── Dashboard.fxml               # Main dashboard layout
    │   └── Transfer.fxml                # Transfer screen layout
    └── CSS/
        └── style.css                    # Global stylesheet for all screens
```

---

## Database Setup

The application requires a **MySQL** server running locally on port `3306`.

### 1. Create the database and tables

Run the provided SQL script in your MySQL client (MySQL Workbench, DBeaver, or CLI):

```bash
mysql -u root -p < finverse.sql
```

Or paste the contents of `finverse.sql` directly into your MySQL client. The script:
- Creates the `finverse_db` database.
- Creates three tables: `User`, `Wallet`, and `Transaction` with appropriate foreign-key constraints.

### 2. Configure the connection

Open `src/main/java/org/example/db/DBConnection.java` and update the credentials to match your local MySQL setup:

```java
private static final String URL      = "jdbc:mysql://127.0.0.1:3306/finverse_db";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password";
```

---

## Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Java JDK | 21 |
| Apache Maven | 3.8+ |
| MySQL Server | 8.0+ |

---

## How to Run

### Option A — Maven (recommended)

```bash
# 1. Clone / unzip the project
cd FinverseWallet

# 2. Install dependencies and compile
mvn clean install

# 3. Launch the application
mvn javafx:run
```

### Option B — IDE (IntelliJ IDEA)

1. Open the `FinverseWallet` folder as a Maven project.
2. Let IntelliJ download all dependencies automatically.
3. Right-click `Main.java` → **Run 'Main.main()'**.

> **Tip:** If JavaFX is not bundled with your JDK, ensure the `javafx-maven-plugin` in `pom.xml` is present (it already is). IntelliJ may also require adding `--add-opens` VM options — the plugin handles this when run via `mvn javafx:run`.

---

## Key Dependencies (`pom.xml`)

| Dependency | Version | Purpose |
|------------|---------|---------|
| `javafx-controls` | 21 | UI controls (Button, TableView, Label, …) |
| `javafx-fxml` | 21 | FXML layout loading |
| `mysql-connector-j` | 8.3.0 | JDBC driver for MySQL |
| `jbcrypt` | 0.4 | BCrypt password hashing |

---

## Application Flow

```
Login Screen
    │
    ├─ New user ──▶ Register Screen ──▶ Auto-create Wallet ──▶ Login Screen
    │
    └─ Existing user ──▶ Dashboard
                              │
                              ├─ Deposit / Withdraw (inline on dashboard)
                              ├─ View Transaction History (TableView)
                              └─ Transfer ──▶ Transfer Screen ──▶ Dashboard
```
**FINAL PROJECT RECORDING YOUTUBE LINK :** https://youtu.be/Od_ap1UFFhA
**URL GITHUB REPOSITORY** : https://github.com/kashafqamar12345-art/Finverse-Wallet

## Notes

- Passwords are never stored in plain text; BCrypt with a generated salt is used for all password storage and verification.
- A wallet (PKR, starting balance PKR 0.00) is automatically created for every newly registered user.
- Business rules enforced by `WalletService`: minimum balance of PKR 100 must remain after any withdrawal or transfer; single deposit capped at PKR 100,000; minimum transfer amount is PKR 10.
- Session state (logged-in user and wallet) is held in the static `SessionManager` and cleared on logout.
