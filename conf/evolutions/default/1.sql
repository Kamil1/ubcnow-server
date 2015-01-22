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
    summary VARCHAR(255),
    link VARCHAR(255),
    time DATE,
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

CREATE TABLE group_interests (
    id INTEGER NOT NULL AUTO_INCREMENT,
    gid INTEGER NOT NULL,
    iid INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (gid) REFERENCES groups(id),
    FOREIGN KEY (iid) REFERENCES interests(id)
);

-- FIXME: Placeholder data

INSERT INTO groups (name, concrete)
    VALUES ('Example Group', true);
INSERT INTO blips (gid, title, summary, link)
    VALUES (1, 'Blip 1 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO blips (gid, title, summary, link)
    VALUES (1, 'Blip 2 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO blips (gid, title, summary, link)
    VALUES (1, 'Blip 3 Title', 'This is a summary of a blip.', 'http://alphaproject.me');
INSERT INTO interests (name)
    VALUES ('Example Interest');
INSERT INTO group_interests (gid, iid)
    VALUES (1, 1);

# --- !Downs

DROP TABLE groups;
DROP TABLE blips;
DROP TABLE interests;
DROP TABLE group_interests;
