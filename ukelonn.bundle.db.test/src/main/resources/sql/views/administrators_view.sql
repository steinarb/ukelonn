create view administrators_view (administrator_id, user_id, username, first_name, last_name)
as select administrators.administrator_id, users.user_id, username, first_name, last_name
 from users
 join administrators on administrators.user_id=users.user_id
group by administrators.administrator_id, users.user_id, username, first_name, last_name
