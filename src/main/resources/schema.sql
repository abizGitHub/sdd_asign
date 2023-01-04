CREATE SEQUENCE HIBERNATE_SEQUENCE;
CREATE SEQUENCE ACCOUNT_NUMBER_SEQUENCE;

CREATE TABLE USER_DETAIL (
                      id          INTEGER PRIMARY KEY,
                      username VARCHAR(64) NOT NULL,
                      password VARCHAR(64) NOT NULL,
                      roles VARCHAR(64) );

CREATE TABLE ACCOUNT (
                         id          INTEGER PRIMARY KEY,
                         balance NUMBER (19, 6),
                         account_Holder VARCHAR(64) NOT NULL,
                         account_Type NUMBER NOT NULL,
                         create_Date DATE NOT NULL,
                         account_Number VARCHAR(64) NOT NULL,
                         blocked NUMBER(1) NOT NULL);

CREATE TABLE FINANCE_TRANSACTION (
                                     id          INTEGER PRIMARY KEY,
                                     amount NUMBER (19, 6),
                                      transferor_Account_ID INTEGER ,
                                      transferee_Account_ID INTEGER ,
                                     transfer_Date DATE NOT NULL,
                                     balance NUMBER (19, 6),
                                     Operation_Type NUMBER(1) NOT NULL,
                                     operator_Name VARCHAR(64),
                                     description VARCHAR(1024)
);

ALTER TABLE FINANCE_TRANSACTION ADD FOREIGN KEY (transferee_Account_ID) REFERENCES ACCOUNT(ID);
