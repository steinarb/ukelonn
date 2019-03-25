--liquibase formatted sql
--changeset sb:example_administrators
insert into administrators (username) values ('admin');
insert into administrators (username) values ('on');
insert into administrators (username) values ('kn');
--rollback truncate table administrators; alter table administrators alter administrator_id restart with 1;
