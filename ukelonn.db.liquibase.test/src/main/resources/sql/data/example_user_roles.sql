--liquibase formatted sql
-- Will run successfully with the authservice users table schema (and fail on the original ukelonn users schema)
--changeset sb:example_user_roles failOnError:false
insert into user_roles (user_role, username) values ('ukelonnuser','admin');
insert into user_roles (user_role, username) values ('ukelonnuser','on');
insert into user_roles (user_role, username) values ('ukelonnuser','kn');
insert into user_roles (user_role, username) values ('ukelonnuser','jad');
insert into user_roles (user_role, username) values ('ukelonnuser','jod');
insert into user_roles (user_role, username) values ('ukelonnadmin','admin');
insert into user_roles (user_role, username) values ('ukelonnadmin','on');
insert into user_roles (user_role, username) values ('ukelonnadmin','kn');
--rollback delete from user_roles; alter table user_roles alter user_role_id restart with 1;
