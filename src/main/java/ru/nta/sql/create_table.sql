create table post
(
    id      serial primary key,
    name    varchar(100),
    text    varchar(1000),
    link    varchar(200) unique,
    created varchar(200)
);