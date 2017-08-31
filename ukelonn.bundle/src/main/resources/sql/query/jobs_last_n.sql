select t1.transaction_id, t1.account_id, t1.transaction_type_id, tt.transaction_type_name, tt.transaction_is_work, tt.transaction_is_wage_payment, t1.transaction_amount, t1.transaction_time, t2.transaction_time is not null and t1.transaction_time<t2.transaction_time as paid_out from transactions as t1
  join transaction_types as tt on tt.transaction_type_id=t1.transaction_type_id
  left outer join (select t.* from
                   (select t3.transaction_id, t3.account_id, t3.transaction_time, sum(t4.transaction_amount) as balance from transactions as t3
                     join transactions as t4 on t4.account_id=t3.account_id and t3.transaction_time >= t4.transaction_time
                    where t3.account_id=?
                    group by t3.transaction_id, t3.account_id, t3.transaction_time
                    order by t3.transaction_time desc) t
                   where balance=0 fetch next 1 rows only) as t2 on t1.account_id=t2.account_id
 where t1.transaction_id in
 (select transaction_id from transactions
  join transaction_types on transaction_types.transaction_type_id=transactions.transaction_type_id
  where account_id=? and transaction_types.transaction_is_work order by transaction_id desc fetch next %d rows only)
 group by t1.transaction_id, t1.account_id, t1.transaction_amount, t1.transaction_type_id, t1.transaction_time, tt.transaction_type_name, tt.transaction_is_work, tt.transaction_is_wage_payment, t2.transaction_time
 order by t1.transaction_time
