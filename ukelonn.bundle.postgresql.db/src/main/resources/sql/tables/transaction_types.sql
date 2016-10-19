create table transaction_types (
 transaction_type_id serial primary key,
 transaction_type_name varchar(256) not null,
 transaction_amount decimal,
 transaction_is_work boolean not null default false,
 transaction_is_wage_payment boolean not null default false
)

