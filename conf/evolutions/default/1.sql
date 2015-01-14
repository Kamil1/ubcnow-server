# Schema v1

# --- !Ups

create table Groups (
    id integer not null auto_increment,
    primary key (id)
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
    primary key (id),

--  FIXME: finish groups schema
--  foreign key (gid) references Groups(id) 
);

insert into Groups values ();
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 1 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 2 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
insert into Blips (gid, title, summary, link)
    values (0, 'Blip 3 Title', 'This is a summary of a blip.', 'http://alphaproject.me');

# --- !Downs

drop table Groups;
drop table Blips;
