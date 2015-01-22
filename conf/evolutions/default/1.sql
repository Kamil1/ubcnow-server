# Schema v1

# --- !Ups

CREATE TABLE Groups (
    gid INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    interest INTEGER NOT NULL,
    concrete BOOLEAN NOT NULL
);

CREATE TABLE Blips (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    gid INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(255),
    link VARCHAR(255),
    time DATE,
    address VARCHAR(255),
    lat FLOAT,
    lng FLOAT,
);

CREATE TABLE Interest (
    iid INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE GroupInterests (
    gid INTEGER REFERENCES Groups (gid),
    iid INTEGER REFERENCES Interest (iid),
    PRIMARY KEY (gid, iid)
);


INSERT INTO Groups (gid, name, interest, concrete)
    VALUES (0, 'This is a name', 0, true);
INSERT INTO Blips (gid, title, summary, link)
    VALUES (0, 'Blip 1 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO Blips (gid, title, summary, link)
    VALUES (0, 'Blip 2 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO Blips (gid, title, summary, link)
    VALUES (0, 'Blip 3 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO Interest (iid, name)
    VALUES (0, 'This is a name');

# --- !Downs

DROP TABLE Groups;
DROP TABLE Blips;
DROP TABLE Interest;
