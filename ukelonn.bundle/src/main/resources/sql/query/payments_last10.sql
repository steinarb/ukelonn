select * from transactions
 where transaction_id in
 (select transaction_id from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
  where account_id=%d and transaction_types.transaction_is_work order by transaction_id desc fetch next 10 rows only)
 or transaction_id in
 (select transaction_id from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
  where account_id=%d and transaction_types.transaction_is_wage_payment order by transaction_id desc fetch next 10 rows only)
