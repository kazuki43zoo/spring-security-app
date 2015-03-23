DROP TABLE IF EXISTS account;

CREATE TABLE account(
    username VARCHAR(128),
    password VARCHAR(60),
    first_name VARCHAR(128),
    last_name VARCHAR(128),
    CONSTRAINT pk_tbl_account PRIMARY KEY (username)
);