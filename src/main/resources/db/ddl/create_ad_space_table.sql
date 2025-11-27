CREATE SEQUENCE IF NOT EXISTS ad_space_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE if NOT EXISTS ad_space (
    id INTEGER PRIMARY KEY DEFAULT nextval('ad_space_id_seq'),
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    price_per_day NUMERIC(12,2) NOT NULL,
    availability_status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    CONSTRAINT type CHECK (type IN ('BILLBOARD','BUS_STOP','MALL_DISPLAY','TRANSIT_AD')),
    CONSTRAINT availability_status CHECK (availability_status IN ('AVAILABLE','BOOKED','MAINTENANCE'))
);

