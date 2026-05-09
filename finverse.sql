CREATE DATABASE finverse_db;
USE finverse_db;

-- Users Table
CREATE TABLE User (
    User_ID       INT AUTO_INCREMENT PRIMARY KEY,
    Username      VARCHAR(50) UNIQUE NOT NULL,
    Email         VARCHAR(100) UNIQUE NOT NULL,
    Password_Hash VARCHAR(255) NOT NULL,
    Created_At    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Wallet Table
CREATE TABLE Wallet (
    Wallet_ID  INT AUTO_INCREMENT PRIMARY KEY,
    User_ID    INT NOT NULL,
    Balance    DECIMAL(15,2) DEFAULT 0.00,
    Currency   VARCHAR(10) DEFAULT 'PKR',
    Updated_At DATETIME DEFAULT CURRENT_TIMESTAMP 
               ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (User_ID) REFERENCES User(User_ID)
);

-- Transaction Table
CREATE TABLE Transaction (
    Trans_ID    INT AUTO_INCREMENT PRIMARY KEY,
    Wallet_ID   INT NOT NULL,
    Type        ENUM('DEPOSIT','WITHDRAW','TRANSFER') NOT NULL,
    Amount      DECIMAL(15,2) NOT NULL,
    Other_Party VARCHAR(100),
    Timestamp   DATETIME DEFAULT CURRENT_TIMESTAMP,
    Status      ENUM('SUCCESS','FAILED') DEFAULT 'SUCCESS',
    FOREIGN KEY (Wallet_ID) REFERENCES Wallet(Wallet_ID)
);
Use finverse_db;
SELECT * from user;
SELECT * from Wallet;
SELECT * from transaction;
select Balance , u.User_ID ,Username from User u INNER JOIN Wallet w ON u.user_id = w.user_id;
DELETE from Wallet where User_ID =3;
DELETE from user where User_ID =3;
