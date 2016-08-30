create table administrators (
 administrator_id integer not null generated always as identity (start with 1, increment by 1),
 user_id integer not null references users(user_id),
 primary key (administrator_id)
)
