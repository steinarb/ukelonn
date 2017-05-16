create table accounts (
 account_id integer not null generated always as identity (start with 1, increment by 1),
 user_id integer references users(user_id),
 primary key (account_id)
)
