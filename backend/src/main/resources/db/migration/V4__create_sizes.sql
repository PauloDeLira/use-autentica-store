CREATE TABLE sizes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(20) NOT NULL UNIQUE,
    display_order INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);
