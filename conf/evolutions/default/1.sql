# Schema v1

# --- !Ups

CREATE TABLE groups (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    concrete BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE blips (
    id INTEGER NOT NULL AUTO_INCREMENT,
    gid INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    link VARCHAR(255),
    startTime DATE(255),
    endTime DATE(255),
    address VARCHAR(255),
    lat FLOAT,
    lng FLOAT,
    PRIMARY KEY (id),
    FOREIGN KEY (gid) REFERENCES groups(id)
);

CREATE TABLE interests (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id INTEGER NOT NULL AUTO_INCREMENT,
    puid VARCHAR(255) NOT NULL,
    studentNumber INTEGER,
    affiliation VARCHAR(255) NOT NULL,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE group_interests (
    id INTEGER NOT NULL AUTO_INCREMENT,
    gid INTEGER NOT NULL,
    iid INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (gid) REFERENCES groups(id),
    FOREIGN KEY (iid) REFERENCES interests(id)
);

CREATE TABLE user_interests (
    id INTEGER NOT NULL AUTO_INCREMENT,
    puid INTEGER NOT NULL,
    iid INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (puid) REFERENCES users(id),
    FOREIGN KEY (iid) REFERENCES interests(id)
);

CREATE TABLE user_groups (
    id INTEGER NOT NULL AUTO_INCREMENT,
    puid INTEGER NOT NULL,
    gid INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (puid) REFERENCES users(id),
    FOREIGN KEY (gid) REFERENCES groups(id)
);

-- FIXME: Placeholder data

INSERT INTO groups (name, concrete)
    VALUES ('Example Group 1', true);
INSERT INTO groups (name, concrete)
    VALUES ('Example Group 2', true);
INSERT INTO blips (gid, title, summary, link, startTime)
    VALUES (1, 'Blip 1 Title', 'This is a summary of a blip.', 'http://alphaproject.me', NOW());
INSERT INTO blips (gid, title, summary, link, lat, lng)
    VALUES (1, 'Blip 2 Title', 'This is a summary of a blip.', 'http://alphaproject.me', -123.1, 49.28);
INSERT INTO blips (gid, title, summary, link)
    VALUES (1, 'Blip 3 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO interests (name)
    VALUES ('Example Interest 1');
INSERT INTO interests (name)
    VALUES ('Example Interest 2');
INSERT INTO interests (name)
    VALUES ('Example Interest 3');
INSERT INTO group_interests (gid, iid)
    VALUES (1, 1);
INSERT INTO group_interests (gid, iid)
    VALUES (1, 2);
INSERT INTO group_interests (gid, iid)
    VALUES (2, 1);

# --- !Downs

DROP TABLE groups;
DROP TABLE blips;
DROP TABLE interests;
DROP TABLE group_interests;
