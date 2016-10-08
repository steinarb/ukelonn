create table administrators (
 administrator_id serial primary key,
 user_id integer not null references users(user_id)
)
