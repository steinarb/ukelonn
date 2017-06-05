--liquibase formatted sql
--changeset sb:initial_administrators
insert into administrators (user_id) values (1);

