CREATE TABLE IF NOT EXISTS sent_email_notification_record(
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    provider_id text NOT NULL,
    provider varchar(256) NOT NULL,
    status text NOT NULL,
    metadata jsonb
);