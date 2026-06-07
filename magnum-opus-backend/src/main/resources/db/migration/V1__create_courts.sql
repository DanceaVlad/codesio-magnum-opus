create table courts (
    id uuid not null,
    name varchar(255) not null,
    type varchar(16) not null,
    created_at timestamp with time zone not null,
    constraint pk_courts primary key (id),
    constraint uk_courts_name unique (name),
    constraint ck_courts_type check (type in ('INDOOR', 'OUTDOOR'))
);
