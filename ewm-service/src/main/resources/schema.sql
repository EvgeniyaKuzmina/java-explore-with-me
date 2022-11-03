CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100),
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORIES_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(300)                            NOT NULL,
    email VARCHAR(500)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      TEXT                                    NOT NULL,
    event_id  BIGINT                                  NOT NULL,
    author_id BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    status       VARCHAR(20),
    requester_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id     BIGINT,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT FK_REQUEST_ON_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat float4,
    lon float4,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    description        VARCHAR(7000),
    state              VARCHAR(20),
    category_id        BIGINT                                  NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    comment_id         BIGINT,
    location_id        BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    paid               BOOLEAN                                 NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    confirmed_requests BIGINT,
    views              BIGINT,
    participant_limit  BIGINT                                  NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT FK_INITIATOR_OF_EVENT_ON_USER FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT FK_EVENT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT FK_EVENT_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id),
    CONSTRAINT FK_EVENT_ON_LOCATION FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title  VARCHAR(120)                            NOT NULL,
    pinned BOOLEAN,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT UQ_COMPILATION_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    compilation_id BIGINT                                  NOT NULL,
    event_id       BIGINT                                  NOT NULL,

    CONSTRAINT pk_compilation_event PRIMARY KEY (id),
    CONSTRAINT FK_COMPILATION_EVENT_ON_COMPILATION FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT FK_COMPILATION_EVENT_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id)
);