--liquibase formatted sql
--changeset sb:example_accounts
insert into accounts (username) values ('admin');
insert into accounts (username) values ('on');
insert into accounts (username) values ('kn');
insert into accounts (username) values ('jad');
insert into accounts (username) values ('jod');
--rollback delete from accounts; alter table accounts alter account_id restart with 1;
