insert into transactions (account_id, transaction_type_id, transaction_amount)
select account_id, 4, 0.0 from accounts
where user_id=?
