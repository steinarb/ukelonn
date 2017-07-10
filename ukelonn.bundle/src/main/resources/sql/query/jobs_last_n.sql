select * from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
 where transaction_id in
 (select transaction_id from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
  where account_id=? and transaction_types.transaction_is_work order by transaction_id desc fetch next ? rows only)
 order by transaction_id
