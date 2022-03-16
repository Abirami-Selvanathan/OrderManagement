create table if not exists users
(
    id int8 not null,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255),
    password varchar(255)
    primary key (id)
);

create table if not exists books
(
    id int8 not null,
    name varchar(255),
    quantity int not null,
    price float8 not null,
    created_date date not null,
    version bigint not null,
    primary key (id)
);

create table if not exists tokens
(
    id int not null,
    token varchar(255) not null,
    created_date date not null,
    user_id int8 not null,
    primary key (id)
);

create table if not exists orders
(
    id int8 not null,
    created_date date not null,
    user_id int8 not null,
    primary key (id)
);
