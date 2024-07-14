create table if not exists JoltTemplate
(
    id           bigint auto_increment
    primary key,
    name         varchar(255)                        null,
    description  varchar(255)                        null,
    inputjson    longtext                            null,
    specjson     longtext                            null,
    outputjson   longtext                            null,
    tags         varchar(255)                        null,
    modifieddate timestamp default CURRENT_TIMESTAMP not null
    );

create table if not exists TransformationHistory
(
    id          bigint auto_increment
    primary key,
    user_id     varchar(255)                        null,
    input_json  longtext                            null,
    spec_json   longtext                            not null,
    output_json longtext                            null,
    timestamp   timestamp default CURRENT_TIMESTAMP null
    );

create table if not exists User
(
    id         bigint auto_increment
    primary key,
    email      varchar(255) null,
    name       varchar(255) null,
    picture    varchar(255) null,
    subscribed varchar(255) null
    );

