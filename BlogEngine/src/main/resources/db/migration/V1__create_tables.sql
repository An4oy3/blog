drop table if exists captcha_codes;
drop table if exists global_settings;
drop table if exists post_comments;
drop table if exists post_votes;
drop table if exists posts;
drop table if exists tag2post;
drop table if exists tags;
drop table if exists users;
create table captcha_codes (
        id integer not null auto_increment,
        code varchar(255),
        secret_code varchar(255),
        time datetime,
        primary key (id));
create table global_settings (
        id integer not null auto_increment,
        code varchar(255),
        name varchar(255),
        value varchar(255),
        primary key (id));
create table post_comments (
        id integer not null auto_increment,
        text varchar(255),
        time datetime,
        parent_id_id integer,
        post_id integer,
        user_id integer,
        primary key (id));
create table post_votes (
        id integer not null auto_increment,
        time datetime,
        value tinyint not null,
        post_id integer,
        user_id integer,
        primary key (id));
create table posts (
        id integer not null auto_increment,
        is_active tinyint,
        moderation_status varchar(255),
        text varchar(255),
        title varchar(255),
        time datetime,
        view_count integer,
        moderator_id integer,
        user_id integer,
        primary key (id));
create table tag2post (
        id integer not null auto_increment,
        post_id integer,
        tag_id integer,
        primary key (id));
create table tags (
        id integer not null auto_increment,
        name varchar(255),
        primary key (id));
create table users (
        id integer not null auto_increment,
        code varchar(255),
        email varchar(255),
        is_moderator tinyint,
        name varchar(255),
        password varchar(255),
        photo varchar(255),
        reg_time datetime,
        primary key (id));
alter table post_comments add constraint FK85f3w1p3os8irvfa0imsuqurg foreign key (parent_id_id) references post_comments (id);
alter table post_comments add constraint FKaawaqxjs3br8dw5v90w7uu514 foreign key (post_id) references posts (id);
alter table post_comments add constraint FKsnxoecngu89u3fh4wdrgf0f2g foreign key (user_id) references users (id);
alter table post_votes add constraint FK9jh5u17tmu1g7xnlxa77ilo3u foreign key (post_id) references posts (id);
alter table post_votes add constraint FK9q09ho9p8fmo6rcysnci8rocc foreign key (user_id) references users (id);
alter table posts add constraint FK6m7nr3iwh1auer2hk7rd05riw foreign key (moderator_id) references users (id);
alter table posts add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (user_id) references users (id);
alter table tag2post add constraint FKpjoedhh4h917xf25el3odq20i foreign key (post_id) references posts (id);
alter table tag2post add constraint FKjou6suf2w810t2u3l96uasw3r foreign key (tag_id) references tags (id);