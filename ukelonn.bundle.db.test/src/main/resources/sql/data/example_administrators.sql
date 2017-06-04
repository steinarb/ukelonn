--liquibase formatted sql
--changeset sb:example_administrators
insert into administrators (user_id) values (1);
insert into administrators (user_id) values (2);
insert into administrators (user_id) values (3);
