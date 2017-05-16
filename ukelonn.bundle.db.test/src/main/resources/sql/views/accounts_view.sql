create view accounts_view (account_id, user_id, username, first_name, last_name, balance)
as select accounts.account_id, users.user_id, username, first_name, last_name, SUM(transaction_amount) as balance
 from users
 join accounts on accounts.user_id=users.user_id
 join transactions on transactions.account_id=accounts.account_id
group by accounts.account_id, users.user_id, username, first_name, last_name
