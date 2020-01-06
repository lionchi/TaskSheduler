insert into task (id, complexity, create_date, deadline, description, is_quickly, is_read, name, status, type, user_id)
values (1, 'NORMAL', now(), '2018-10-15', 'bla bla', true, false, 'Задача №1', 'FAMILIARIZATION', 'NEW', 2);

insert into task (id, complexity, create_date, deadline, description, is_quickly, is_read, name, status, type, user_id)
values (2, 'EASY', now(), '2018-10-15', 'bla bla', false, false, 'Задача №2', 'FAMILIARIZATION', 'BAG', 2);

insert into task (id, complexity, create_date, deadline, description, is_quickly, is_read, name, status, type, user_id)
values (3, 'NORMAL', now(), '2018-10-15', 'bla bla', false, false, 'Задача №3', 'IMPLEMENTATION', 'BAG', 3);

insert into task (id, complexity, create_date, deadline, description, is_quickly, is_read, name, status, type, user_id)
values (4, 'HARD', now(), '2018-10-15', 'bla bla', true, false, 'Задача №4', 'ACTIVE', 'NEW', 3);