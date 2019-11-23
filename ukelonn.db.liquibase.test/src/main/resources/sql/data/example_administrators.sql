--liquibase formatted sql
-- Will run successfully with the original ukelonn users table schema (and fail on the authservice schema)
--changeset sb:example_administrators failOnError:false
insert into administrators (username) values ('admin');
insert into administrators (username) values ('on');
insert into administrators (username) values ('kn');
--rollback truncate table administrators; alter table administrators alter administrator_id restart with 1;
