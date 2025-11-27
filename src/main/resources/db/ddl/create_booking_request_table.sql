CREATE SEQUENCE IF NOT EXISTS booking_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE if NOT EXISTS booking_request (
    id INTEGER PRIMARY KEY,
    uuid UUID NOT NULL,
    ad_space_id INTEGER NOT NULL,
    advertiser_name VARCHAR(50) NOT NULL,
    advertiser_email VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_cost NUMERIC(14,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT status CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    FOREIGN KEY (ad_space_id) REFERENCES ad_space(id)
);

