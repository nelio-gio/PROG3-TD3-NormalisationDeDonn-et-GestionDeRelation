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




