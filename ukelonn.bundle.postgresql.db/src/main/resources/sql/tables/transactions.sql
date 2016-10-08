create table transactions (
 transaction_id serial primary key,
 account_id integer not null references accounts(account_id),
 transaction_type_id integer not null references transaction_types(transaction_type_id),
 transaction_time timestamp not null default current_timestamp,
 transaction_amount real not null
)
