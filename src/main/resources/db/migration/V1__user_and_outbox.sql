CREATE
    TABLE
        IF NOT EXISTS user_account(
            id uuid PRIMARY KEY,
            username VARCHAR(128) NOT NULL UNIQUE,
            email text NOT NULL,
            password text NOT NULL,
            roles text [],
            enabled BOOLEAN
        );

CREATE
    TABLE
        IF NOT EXISTS outbox(
            id uuid PRIMARY KEY,
            aggregate text NOT NULL,
            aggregate_id text NOT NULL,
            event_type text NOT NULL,
            payload text NOT NULL,
            insert_order SERIAL NOT NULL
        );

CREATE
    INDEX IF NOT EXISTS outbox_order ON
    outbox(
        insert_order ASC
    );