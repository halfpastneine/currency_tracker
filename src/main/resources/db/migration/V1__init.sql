create table alerts (
    id bigserial primary key,
    email varchar(255) not null,
    api_name varchar(64) not null,
    currency_type varchar(64) not null,
    target_price numeric(24,8) not null,
    active boolean not null default true,
    last_trigger timestamptz not null
);

create table current_price(
    id            bigserial primary key,
    currency_type varchar(64)    not null,
    current_price numeric(24, 8) not null,
    time_fetched  timestamptz    not null
);