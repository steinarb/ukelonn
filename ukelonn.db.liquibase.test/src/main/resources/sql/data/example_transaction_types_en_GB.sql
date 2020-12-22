--liquibase formatted sql
--changeset sb:example_transaction_types
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Vacuuming 1st floor',4.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Vacuuming basement',4.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Recycle paper',3.5,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Pay to account',null,false,true);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Extra data for mobile',4.9,false,true);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Vacuuming 2nd floor',3,true,false);
--rollback delete from transaction_types; alter table transaction_types alter transaction_type_id restart with 1;
