CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS request
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000)               NOT NULL,
    requestor   BIGINT                      NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_request_to_user FOREIGN KEY (requestor) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    available   BOOLEAN       NOT NULL,
    owner       BIGINT,
    request_id  BIGINT,
    CONSTRAINT fk_item_to_user FOREIGN KEY (owner) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_to_request FOREIGN KEY (request_id) REFERENCES request (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL,
    booker_id  BIGINT                      NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    CONSTRAINT fk_booking_to_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_to_user FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR(1000)               NOT NULL,
    item_id   BIGINT                      NOT NULL,
    author_id BIGINT                      NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comment_to_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_to_user FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);
