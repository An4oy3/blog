insert into users(id, is_moderator, reg_time, name, email, password) VALUES('1', '1', '2021-07-24', 'Федор', 'fedor@gmail.com', '123');
insert into users(id, is_moderator, reg_time, name, email, password) VALUES('2', '0', '20210724', 'Петр', 'petya@gmail.com', '321');
insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', '1', 'NEW', '1', '1', '20210724', 'Заголовок первого поста', 'Текст первого поста', '22');
insert into post_comments(post_id, user_id, time, text) VALUES('1', '2', '20210724', 'Первый комментарий');
insert into tags(name) VALUES('Первый');
insert into tags(name) VALUES('First post');
insert into tag2post(post_id, tag_id) VALUES('1', '1');
insert into tag2post(post_id, tag_id) VALUES('1', '2');
insert into users(is_moderator, reg_time, name, email, password) VALUES('1', '2021-07-26', 'Артем', 'artem@gmail.com', '123');
insert into users(is_moderator, reg_time, name, email, password) VALUES('0', '2021-07-24', 'Петр', 'petya@gmail.com', '321');
insert into users(is_moderator, reg_time, name, email, password) VALUES('1', '2021-07-29', 'Александр', 'petya@gmail.com', '321');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'NEW', '1', '4', '20210724', 'Заголовок первого поста', 'Текст первого поста', '15');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'NEW', '1', '1', '20210814', 'Заголовок второго поста', 'Текст второго поста', '23');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'NEW', '1', '2', '20210801', 'Заголовок третьего поста', 'Текст третьего поста', '6');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'NEW', '1', '4', '20210730', 'Заголовок четвертого поста', 'Текст четвертого поста', '19');
insert into post_comments(post_id, user_id, time, text) VALUES('4', '1', '20210724', 'Второй комментарий');
insert into post_comments(post_id, user_id, time, text) VALUES('3', '2', '20210724', 'Третий комментарий');
