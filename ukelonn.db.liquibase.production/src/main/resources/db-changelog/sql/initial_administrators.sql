--liquibase formatted sql
--changeset sb:initial_administrators
insert into administrators (username) values ('admin');
