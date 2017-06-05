--liquibase formatted sql
--changeset sb:initial_transaction_types
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Støvsuging 1. etasje',45,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Støvsuging kjeller',45,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Gå med resirk',35,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Inn på konto',null,false,true);

