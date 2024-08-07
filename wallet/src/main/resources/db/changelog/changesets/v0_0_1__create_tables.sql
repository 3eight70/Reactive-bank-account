CREATE TABLE t_users(
    id UUID PRIMARY KEY ,
    login VARCHAR(255) NOT NULL UNIQUE ,
    email VARCHAR(255) NOT NULL UNIQUE ,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE t_accounts(
    id UUID PRIMARY KEY ,
    user_id UUID NOT NULL ,
    balance DECIMAL NOT NULL ,
    CONSTRAINT fk_account_user_id FOREIGN KEY (user_id) REFERENCES t_users(id)
);

CREATE TABLE t_transactions(
    id UUID PRIMARY KEY ,
    account_id_from UUID NOT NULL ,
    account_id_where UUID NOT NULL ,
    amount DECIMAL NOT NULL ,
    transaction_time timestamp NOT NULL ,
    CONSTRAINT fk_transaction_account_from_id FOREIGN KEY (account_id_from) REFERENCES t_accounts(id),
    CONSTRAINT fk_transaction_account_where_id FOREIGN KEY (account_id_where) REFERENCES t_accounts(id)
);