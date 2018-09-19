select t.*, tt.*, false as paid_out from transactions t
  join transaction_types tt on tt.transaction_type_id=t.transaction_type_id
 where t.transaction_id in
 (select transaction_id from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
  where account_id=? and transaction_types.transaction_is_wage_payment order by transaction_id desc fetch next %d rows only)
 order by t.transaction_id
