drop database if exists eshop;

create database eshop;

use eshop;

create table customers (
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,

    primary key(name)
);

create table orders (
    order_id char(8) not null,
    delivery_id varchar(128) not null default "processing",
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,
    status enum('pending', 'dispatched')  default 'pending',
    orderDate char(64),

    primary key(order_id),
    constraint fk_name
        foreign key(name) references customers(name)
);

create index idx_delivery_id on orders (delivery_id);

create table lineItems (
    item_id Integer auto_increment,
    order_id char(8) not null,
    item varchar(32) not null,
    quantity integer not null,

    primary key(item_id),
    constraint fk_order_id
        foreign key(order_id) references orders(order_id)
);

create table order_status (
    status_id Integer auto_increment,
    delivery_id varchar(128) not null default "processing",
    status enum('pending', 'dispatched')  default 'pending',
    status_update date not null,

    primary key(status_id),
    constraint fk_delivery_id
        foreign key(delivery_id) references orders(delivery_id)
);

insert into customers(name, address, email)
values
    ('fred', '201 Cobblestone Lane', 'fredflintstone@bedrock.com'),
    ('sherlock', '221B Baker Street, London', 'sherlock@consultingdetective.org'),
    ('spongebob', '124 Conch Street, Bikini Bottom', 'spongebob@yahoo.com'),
    ('jessica', '698 Candlewood Land, Cabot Cove', 'fletcher@gmail.com'),
    ('dursley', '4 Privet Drive, Little Whinging, Surrey', 'dursley@gmail.com');