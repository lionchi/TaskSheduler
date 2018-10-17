insert into user_role (id, rolename) values (1, 'ADMIN');
insert into user_role (id, rolename) values (2, 'LEAD');
insert into user_role (id, rolename) values (3, 'USER');

insert into user (id, department, fio, login, password, post, enabled, user_role_id)
values (1, 'ИБ', 'Иванов Иван Иванович', 'ivan', md5('123456'), 'администратор', 1, 1);
insert into user (id, department, fio, login, password, post, enabled, user_role_id)
values (2, 'ОИТ', 'Белова Татьяна Сергеевна', 'tas', md5('123456'), 'администратор', 1,1);
insert into user (id, department, fio, login, password, post, enabled, user_role_id)
values (3, 'ИБ', 'Кошелева Анастасия Михайловна', 'mix', md5('123456'), 'сотрудник', 1,3);
insert into user (id, department, fio, login, password, post, enabled, user_role_id)
values (4, 'ИБ', 'Лисовец Мария Игоревна', 'mar', md5('123456'), 'руководитель', 1, 2);


/*insert into user_user_role (user_id, user_role_id) values (1,1);
insert into user_user_role (user_id, user_role_id) values (2,3);
insert into user_user_role (user_id, user_role_id) values (3,3);
insert into user_user_role (user_id, user_role_id) values (4,2);*/


insert into privilege (id, name) values (1, 'create');
insert into privilege (id, name) values (2, 'edit');
insert into privilege (id, name) values (3, 'remove');
insert into privilege (id, name) values (4, 'super_privilege');
insert into privilege (id, name) values (5, 'read');

insert into user_role_privilege (user_role_id, privilege_id) values (1, 1);
insert into user_role_privilege (user_role_id, privilege_id) values (1, 2);
insert into user_role_privilege (user_role_id, privilege_id) values (1, 3);
insert into user_role_privilege (user_role_id, privilege_id) values (1, 4);
insert into user_role_privilege (user_role_id, privilege_id) values (1, 5);
insert into user_role_privilege (user_role_id, privilege_id) values (2, 1);
insert into user_role_privilege (user_role_id, privilege_id) values (2, 2);
insert into user_role_privilege (user_role_id, privilege_id) values (2, 3);
insert into user_role_privilege (user_role_id, privilege_id) values (2, 5);
insert into user_role_privilege (user_role_id, privilege_id) values (3, 2);
insert into user_role_privilege (user_role_id, privilege_id) values (3, 5);
