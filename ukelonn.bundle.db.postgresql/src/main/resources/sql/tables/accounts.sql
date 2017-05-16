create table accounts (
 account_id serial primary key,
 user_id integer references users(user_id)
)
