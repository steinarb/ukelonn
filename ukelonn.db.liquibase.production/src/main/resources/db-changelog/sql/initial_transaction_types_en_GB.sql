--liquibase formatted sql
--changeset sb:initial_transaction_types
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Vacuuming 1st floor',4.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Vacuuming basement',4.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Recycle paper',3.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Pay to account',null,false,true);
