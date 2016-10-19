create table users (
 user_id integer not null generated always as identity (start with 1, increment by 1),
 username varchar(64) not null,
 password varchar(64) not null,
 salt varchar(64) not null,
 email varchar(64) not null,
 first_name varchar(256) not null,
 last_name varchar(256) not null,
 primary key (user_id)
)
