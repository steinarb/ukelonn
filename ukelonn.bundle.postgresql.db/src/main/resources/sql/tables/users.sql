create table users (
 user_id serial primary key,
 username varchar(64) not null,
 password varchar(64) not null,
 email varchar(64) not null,
 first_name varchar(256) not null,
 last_name varchar(256) not null
)
