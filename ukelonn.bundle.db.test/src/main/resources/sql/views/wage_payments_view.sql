create view wage_payments_view (account_id, user_id, username, transaction_time, transaction_type_name, transaction_amount)
as select accounts.account_id, users.user_id, username, transaction_time, transaction_type_name, ABS(transactions.transaction_amount) as transaction_amount
  from users
  join accounts on accounts.user_id=users.user_id
  join transactions on transactions.account_id=accounts.account_id
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id and transaction_is_wage_payment
order by transaction_time desc
