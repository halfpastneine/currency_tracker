create table alerts (
    id bigserial primary key,
    type varchar(16) not null,
    api_name varchar(64) not null,
    base varchar(64) not null,
    quote varchar(64) not null,
    up boolean not null,
    target_price numeric(24,8) not null,
    active boolean not null default true,
    email varchar(255) not null
);

create table current_price(
    id bigserial primary key,
    type varchar(16) not null,
    api_name varchar(64) not null,
    base varchar(64) not null,
    quote varchar(64) not null,
    current_price numeric(24, 8) not null,
    time_fetched timestamptz not null
);