# Schema v1

# --- !Ups

create table Groups (
    gid integer not null auto_increment,
    name varchar(255) not null,
    interest integer not null,
    concrete Boolean not null,
    primary key (gid)
);

create table Blips (
    id integer not null auto_increment,
    gid integer not null,
    title varchar(255) not null,
    summary varchar(255),
    link varchar(255),
    time date,
    address varchar(255),
    lat float,
    lng float,
    primary key (id)

--  FIXME: finish groups schema
--  foreign key (gid) references Groups(id) 
);

create table Interest (
    iid integer not null auto_increment,
    name varchar(255) not null,
    primary key (iid)
);

insert into Groups (gid, name, interest, concrete)
    values (0, 'This is a name', 0, true);
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 1 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 2 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 3 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
insert into Interest (iid, name)
    values (0, 'This is a name');

# --- !Downs

drop table Groups;
drop table Blips;
drop table Interest;
