--liquibase formatted sql
--changeset sb:example_users_authservice_schema failOnError:false
insert into users (username,password,password_salt,email,firstname,lastname) values ('admin','e0/CP6ewTISoWCNfJtUNw/vHTEWl6lXlXGjmE+hBH1o=', 'Iu9wre2JgXuxvRT2MA0CGQ==','admin@localhost','Admin','Istrator');
insert into users (username,password,password_salt,email,firstname,lastname) values ('on','6VuUrsvVkZfxtKwt4tdCmOdXtXCuIbgWhcURzeRpT/g=', 'snKBcs4FMoGZHQlNY2kz5w==','olanordmann2345@gmail.com','Ola','Nordmann');
insert into users (username,password,password_salt,email,firstname,lastname) values ('kn','560/eDyvqQcf8c98w8NEg4W26u5aYzLPlnpaNNTJxYM=', 'pUQ1F2Yjn+kLb/JzOPDG4A==','karinordmann3456@gmail.com','Kari','Nordmann');
insert into users (username,password,password_salt,email,firstname,lastname) values ('jad','adEp0s7cQHrJcIsDTDoFQK8eUAIFGh23wew7Klis1sk=', '78dCvMDECA47YMdtCkgkwQ==','janedoe7896@gmail.com','Jane','Doe');
insert into users (username,password,password_salt,email,firstname,lastname) values ('jod','sU4vKCNpoS6AuWAzZhkNk7BdXSNkW2tmOP53nfotDjE=', '9SFDvohxZkZ9eWHiSEoMDw==','johndoe6789@gmail.com','John','Doe');
--rollback delete from users; alter table users alter user_id restart with 1;
