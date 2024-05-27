CREATE TABLE client (
    login VARCHAR(255) PRIMARY KEY,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(255) UNIQUE,
    birth_date DATE NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100) NOT NULL,
    encoded_password VARCHAR(255) NOT NULL,
    account_size NUMERIC(12, 2) NOT NULL CHECK (account_size >= 0.0),
    CHECK (phone IS NOT NULL OR email IS NOT NULL)
);

CREATE TABLE working_refresh_token (
    jti VARCHAR PRIMARY KEY,
    login VARCHAR(255) NOT NULL
);

CREATE TABLE clients_transfer (
    id SERIAL PRIMARY KEY,
    sender_login VARCHAR(255) NOT NULL,
    receiver_login VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount >= 0.0),
    timestamp timestamp NOT NULL DEFAULT now()
);

insert into client values ('bankowner', '7999', 'milkon@', now(), 'mi', 'sur', 'mid', '$2a$10$M1iwX/qHgs7RLNmEJ4WiBOQj7h.R5K.CqT01NJb/YbHFxubF5j/ti', 123);
insert into client values ('commonuser', '7992', 'milkony@', now(), 'mi', 'sur', 'mid', '$2a$10$M1iwX/qHgs7RLNmEJ4WiBOQj7h.R5K.CqT01NJb/YbHFxubF5j/ti', 123);
