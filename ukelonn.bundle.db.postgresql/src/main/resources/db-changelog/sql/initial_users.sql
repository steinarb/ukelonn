--liquibase formatted sql
--changeset sb:initial_users
insert into users (username,password,salt,email,first_name,last_name) values ('admin','e0/CP6ewTISoWCNfJtUNw/vHTEWl6lXlXGjmE+hBH1o=', 'Iu9wre2JgXuxvRT2MA0CGQ==','admin@localhost','Admin','Istrator');
