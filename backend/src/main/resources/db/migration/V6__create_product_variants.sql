CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products (id),
    size_id UUID NOT NULL REFERENCES sizes (id),
    color_id UUID NOT NULL REFERENCES colors (id),
    sku VARCHAR(100) NOT NULL UNIQUE,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uk_product_variant_size_color UNIQUE (product_id, size_id, color_id)
);

CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);
