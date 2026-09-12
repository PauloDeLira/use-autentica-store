-- ON DELETE CASCADE: clique e historico de interesse, nao conteudo do
-- produto -- excluir o produto (so permitido quando ele nao tem mais
-- variacoes/imagens) tambem descarta o historico de cliques dele.
CREATE TABLE product_click_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_click_events_product_id ON product_click_events (product_id);
CREATE INDEX idx_product_click_events_created_at ON product_click_events (created_at);
