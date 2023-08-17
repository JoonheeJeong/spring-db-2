drop table if exists item CASCADE;
create table item
(
    id          bigint      primary key auto_increment,
    item_name   varchar(10) not null ,
    price       int         not null ,
    quantity    int         not null
);
