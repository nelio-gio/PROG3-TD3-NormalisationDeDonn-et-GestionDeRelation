-- Suppression des anciennes tables (si elles existent)
DROP TABLE IF EXISTS dish_ingredient CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS dish CASCADE;

-- Création de la table Dish (avec selling_price)
CREATE TABLE dish (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      dish_type VARCHAR(50) NOT NULL,
                      selling_price NUMERIC(10,2)
);

-- Création de la table Ingredient (sans id_dish)
CREATE TABLE ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            price NUMERIC(10,2) NOT NULL,
                            category VARCHAR(50) NOT NULL
);

-- Création de la table de jointure ManyToMany
CREATE TABLE dish_ingredient (
                                 id SERIAL PRIMARY KEY,
                                 id_dish INTEGER NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
                                 id_ingredient INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
                                 quantity_required NUMERIC(10,2) NOT NULL,
                                 unit VARCHAR(20) NOT NULL,
                                 CONSTRAINT unique_dish_ingredient UNIQUE (id_dish, id_ingredient)
);

-- Commentaires utiles
COMMENT ON TABLE dish_ingredient IS 'Relation ManyToMany entre plats et ingrédients avec quantité et unité';





            --TD4
ALTER TABLE ingredient
    ADD COLUMN IF NOT EXISTS stock NUMERIC(10,2) DEFAULT 0;


CREATE TYPE IF NOT EXISTS movement_type AS ENUM ('IN', 'OUT');


CREATE TABLE IF NOT EXISTS stock_movement (
                                              id SERIAL PRIMARY KEY,
                                              id_ingredient INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity NUMERIC(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    movement_type movement_type NOT NULL,
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );



-- schema du sujet ANNEXE:
CREATE TABLE IF NOT EXISTS "order" (
                                       id SERIAL PRIMARY KEY,
                                       reference VARCHAR(10) UNIQUE NOT NULL,  -- ex: ORD00001
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_ht NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_ttc NUMERIC(10,2) NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS dish_order (
                                          id SERIAL PRIMARY KEY,
                                          id_order INTEGER NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    id_dish INTEGER NOT NULL REFERENCES dish(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL CHECK (quantity > 0)
    );

CREATE INDEX IF NOT EXISTS idx_dish_order_id_order ON dish_order(id_order);






    --Schema pour la suite du TD (Evaluation)
ALTER TABLE "order"
    ADD COLUMN IF NOT EXISTS order_type VARCHAR(20) NOT NULL DEFAULT 'EAT_IN',
    ADD COLUMN IF NOT EXISTS order_status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    ADD COLUMN IF NOT EXISTS customer_name VARCHAR(255);

-- Création des types enum (si pas déjà fait)
CREATE TYPE order_type_enum AS ENUM ('EAT_IN', 'TAKE_AWAY');
CREATE TYPE order_status_enum AS ENUM ('CREATED', 'READY', 'DELIVERED');
