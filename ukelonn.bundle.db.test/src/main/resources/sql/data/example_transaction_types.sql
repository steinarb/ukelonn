--liquibase formatted sql
--changeset sb:example_transaction_types
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Støvsuging 1. etasje',45,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Støvsuging kjeller',45,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Gå med resirk',35,true,false);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Inn på konto',null,false,true);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Ekstra mobildata',49,false,true);
insert into transaction_types (transaction_type_name,transaction_amount,transaction_is_work,transaction_is_wage_payment) values ('Støvsuging 2. etasje inkl. stort soverom og bad',30,true,false);
--rollback delete from transaction_types; alter table transaction_types alter transaction_type_id restart with 1;
