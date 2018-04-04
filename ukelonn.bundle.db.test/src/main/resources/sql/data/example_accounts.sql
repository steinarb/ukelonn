--liquibase formatted sql
--changeset sb:example_accounts
insert into accounts (user_id) values (1);
insert into accounts (user_id) values (2);
insert into accounts (user_id) values (3);
insert into accounts (user_id) values (4);
insert into accounts (user_id) values (5);
--rollback delete from accounts; alter table accounts alter account_id restart with 1;
