insert into users(is_moderator, reg_time, name, email, password) VALUES('1', '2021-07-24', 'Федор', 'fedor@gmail.com', '123');
insert into users(is_moderator, reg_time, name, email, password) VALUES('0', '2021-07-24', 'Петр', 'petya@gmail.com', '321');
insert into users(is_moderator, reg_time, name, email, password) VALUES('0', '2021-07-26', 'Артем', 'artem@gmail.com', '123');
insert into users(is_moderator, reg_time, name, email, password) VALUES('0', '2021-07-24', 'Максим', 'maks@gmail.com', '321');
insert into users(is_moderator, reg_time, name, email, password) VALUES('1', '2021-07-29', 'Александр', 'alex@gmail.com', '321');

insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'ACCEPTED', '1', '1', '20210724', 'Заголовок первого поста', 'Текст первого поста', '22');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'ACCEPTED', '1', '2', '20210724', 'Заголовок первого поста', 'Текст первого поста', '15');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'ACCEPTED', '1', '3', '20210814', 'Заголовок второго поста', 'Текст второго поста', '23');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'ACCEPTED', '1', '4', '20210801', 'Заголовок третьего поста', 'Текст третьего поста', '6');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'ACCEPTED', '1', '5', '20210730', 'Заголовок четвертого поста', 'Текст четвертого поста', '19');
insert into posts(is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES('1', 'NEW', '1', '3', '20200730', 'Заголовок пятого поста', 'Текст пятого поста', '48');


insert into post_comments(post_id, user_id, time, text) VALUES('1', '2', '20210724', 'Первый комментарий');
insert into post_comments(post_id, user_id, time, text) VALUES('2', '2', '20210724', 'Второй комментарий');
insert into post_comments(post_id, user_id, time, text) VALUES('3', '3', '20210724', 'Третий комментарий');
insert into post_comments(post_id, user_id, time, text) VALUES('1', '1', '20210724', 'Четвертый комментарий');
insert into post_comments(post_id, user_id, time, text) VALUES('1', '3', '20210724', 'Пятый комментарий');

insert into post_votes(time, value, post_id, user_id) VALUES('20210724', '1', '1', '1');
insert into post_votes(time, value, post_id, user_id) VALUES('20210714', '1', '1', '2');
insert into post_votes(time, value, post_id, user_id) VALUES('20210727', '-1', '1', '3');
insert into post_votes(time, value, post_id, user_id) VALUES('20210721', '1', '2', '1');
insert into post_votes(time, value, post_id, user_id) VALUES('20210726', '1', '2', '2');
insert into post_votes(time, value, post_id, user_id) VALUES('20210624', '-1', '2', '3');
insert into post_votes(time, value, post_id, user_id) VALUES('20210324', '-1', '2', '4');
insert into post_votes(time, value, post_id, user_id) VALUES('20210804', '1', '3', '1');
insert into post_votes(time, value, post_id, user_id) VALUES('20210714', '1', '3', '2');
insert into post_votes(time, value, post_id, user_id) VALUES('20210704', '-1', '3', '3');
insert into post_votes(time, value, post_id, user_id) VALUES('20210716', '1', '4', '1');

insert into tags(name) VALUES('Первый');
insert into tags(name) VALUES('Второй');
insert into tags(name) VALUES('Третий');

insert into tag2post(post_id, tag_id) VALUES('1', '1');
insert into tag2post(post_id, tag_id) VALUES('2', '2');
insert into tag2post(post_id, tag_id) VALUES('3', '3');

