create table transaction_types (
 transaction_type_id integer not null generated always as identity (start with 1, increment by 1),
 transaction_type_name varchar(256) not null,
 transaction_amount double,
 transaction_is_work boolean not null default false,
 transaction_is_wage_payment boolean not null default false,
 primary key (transaction_type_id)
)
