CREATE TABLE users (
    user_id VARCHAR(36),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    valid_from DATETIME,
    valid_to DATETIME,
    PRIMARY KEY(user_id, valid_from),
    INDEX(user_id, valid_to)
);

CREATE TABLE citations (
    citation_id VARCHAR(36),
    user_id VARCHAR(36),
    url VARCHAR(255),
    commentary TEXT,
    valid_from DATETIME,
    valid_to DATETIME,
    PRIMARY KEY(citation_id, valid_from),
    INDEX(citation_id, valid_to)
);

CREATE TABLE replies (
    reply_id VARCHAR(36),
    citation_id VARCHAR(36),
    user_id VARCHAR(36),
    reply_commentary TEXT,
    valid_from DATETIME,
    valid_to DATETIME,
    PRIMARY KEY(reply_id, valid_to),
    INDEX(reply_id, valid_to)
);
