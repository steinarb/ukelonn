create table transactions (
 transaction_id integer not null generated always as identity (start with 1, increment by 1),
 account_id integer not null references accounts(account_id),
 transaction_type_id integer not null references transaction_types(transaction_type_id),
 transaction_time timestamp not null default current_timestamp,
 transaction_amount double not null,
 primary key (transaction_id)
)
