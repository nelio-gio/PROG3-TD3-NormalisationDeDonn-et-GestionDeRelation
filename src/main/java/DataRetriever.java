
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {

    // 1. findDishById (TD3) : charge selling_price et les DishIngredient
    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            SELECT dish.id AS dish_id, dish.name AS dish_name, dish_type, dish.selling_price AS dish_selling_price
                            FROM dish
                            WHERE dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setSellingPrice(resultSet.getObject("dish_selling_price") == null
                        ? null : resultSet.getDouble("dish_selling_price"));
                dish.setDishIngredients(findDishIngredientsByDishId(id));
                return dish;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 2. findDishIngredientsByDishId (TD3) : charge la relation ManyToMany
    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        List<DishIngredient> list = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            SELECT di.id, di.id_dish, di.id_ingredient, di.quantity_required, di.unit,
                                   i.id AS ing_id, i.name, i.price, i.category
                            FROM dish_ingredient di
                            JOIN ingredient i ON di.id_ingredient = i.id
                            WHERE di.id_dish = ?;
                            """);
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("ing_id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setIdDish(rs.getInt("id_dish"));
                di.setIdIngredient(rs.getInt("id_ingredient"));
                di.setQuantityRequired(rs.getDouble("quantity_required"));
                di.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                di.setIngredient(ing);
                list.add(di);
            }
            dbConnection.closeConnection(connection);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 3. saveDish (TD3) : utilise selling_price
    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }
                if (toSave.getSellingPrice() != null) {
                    ps.setDouble(2, toSave.getSellingPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 4. createIngredients (TD3) : sans required_quantity
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, category, price)
                        VALUES (?, ?, ?::ingredient_category, ?)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    // 5. findIngredientById (TD4) : charge un ingrédient par ID (avec stock)
    public Ingredient findIngredientById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            SELECT id, name, category, price, stock
                            FROM ingredient
                            WHERE id = ?
                            """);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ing.setPrice(rs.getDouble("price"));
                ing.setStock(rs.getDouble("stock"));
                return ing;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Ingredient not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 6. addStockMovement (TD4) : ajoute un mouvement et met à jour le stock
    public StockMovement addStockMovement(StockMovement movement) {
        try (Connection conn = new DBConnection().getConnection()) {
            String insertSql = """
                    INSERT INTO stock_movement (id_ingredient, quantity, unit, movement_type)
                    VALUES (?, ?, ?, ?::movement_type)
                    RETURNING id, creation_datetime
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, movement.getIdIngredient());
                ps.setDouble(2, movement.getQuantity());
                ps.setString(3, movement.getUnit());
                ps.setString(4, movement.getMovementType());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        movement.setId(rs.getInt("id"));
                        movement.setCreationDatetime(rs.getTimestamp("creation_datetime"));
                    }
                }
            }
            return movement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 7. findStockMovementsByIngredientId (TD4) : liste les mouvements
    public List<StockMovement> findStockMovementsByIngredientId(Integer idIngredient) {
        List<StockMovement> list = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            SELECT id, id_ingredient, quantity, unit, movement_type, creation_datetime
                            FROM stock_movement
                            WHERE id_ingredient = ?
                            ORDER BY creation_datetime DESC;
                            """);
            ps.setInt(1, idIngredient);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));
                sm.setIdIngredient(rs.getInt("id_ingredient"));
                sm.setQuantity(rs.getDouble("quantity"));
                sm.setUnit(rs.getString("unit"));
                sm.setMovementType(rs.getString("movement_type"));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime"));
                list.add(sm);
            }
            dbConnection.closeConnection(connection);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthodes existantes (corrigées pour avoir un return dans tous les chemins)
    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;  // ← Return ajouté ici (corrige le missing return)
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException("Any sequence found for " + tableName + "." + columnName);
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);
        String nextValSql = "SELECT nextval(?)";
        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', GREATEST(1, COALESCE(MAX(%s), 1))) FROM %s",
                sequenceName, columnName, tableName
        );
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }



    // Methode pour l'exercice annexe


    public Order saveOrder(Order orderToSave) {
        if (orderToSave == null || orderToSave.getDishOrders().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un plat");
        }

        // Validation du format de référence (exigence annexe)
        if (orderToSave.getReference() == null || !orderToSave.getReference().matches("^ORD\\d{5}$")) {
            throw new IllegalArgumentException("Référence invalide. Format attendu : ORDXXXXX (ex. ORD00001)");
        }

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            // Vérification stock (TD4)
            for (DishOrder doLine : orderToSave.getDishOrders()) {
                Dish plat = findDishById(doLine.getIdDish());
                if (plat == null) {
                    throw new IllegalArgumentException("Plat introuvable : id=" + doLine.getIdDish());
                }

                List<DishIngredient> ingredients = findDishIngredientsByDishId(doLine.getIdDish());
                for (DishIngredient di : ingredients) {
                    Ingredient ing = di.getIngredient();
                    if (ing.getStock() == null) {
                        ing.setStock(0.0);
                    }
                    double currentStock = ing.getStock() != null ? ing.getStock() : 0.0;
                    double needed = di.getQuantityRequired() * doLine.getQuantity();

                    if (currentStock < needed) {
                        throw new IllegalStateException(
                                "Stock insuffisant pour " + ing.getName() +
                                        " : disponible = " + currentStock + " " + di.getUnit() +
                                        ", requis = " + needed + " " + di.getUnit()
                        );
                    }
                }
            }

            // Insertion commande
            String insertOrderSql = """
            INSERT INTO "order" (reference, total_ht, total_ttc)
            VALUES (?, ?, ?)
            RETURNING id
            """;
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql)) {
                ps.setString(1, orderToSave.getReference());
                ps.setDouble(2, orderToSave.getTotalHT());
                ps.setDouble(3, orderToSave.getTotalTTC());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    orderId = rs.getInt(1);
                }
            }

            // Insertion lignes
            String insertLineSql = """
            INSERT INTO dish_order (id_order, id_dish, quantity)
            VALUES (?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insertLineSql)) {
                for (DishOrder doLine : orderToSave.getDishOrders()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, doLine.getIdDish());
                    ps.setInt(3, doLine.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();

            // Retour de la commande sauvegardée
            Order saved = new Order();
            saved.setId(orderId);
            saved.setReference(orderToSave.getReference());
            saved.setTotalHT(orderToSave.getTotalHT());
            saved.setTotalTTC(orderToSave.getTotalTTC());
            saved.setDishOrders(orderToSave.getDishOrders());
            return saved;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Order findOrderByReference(String reference) {
        try (Connection conn = new DBConnection().getConnection()) {
            String sql = """
            SELECT id, reference, creation_datetime, total_ht, total_ttc
            FROM "order"
            WHERE reference = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, reference);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Order order = new Order();
                        order.setId(rs.getInt("id"));
                        order.setReference(rs.getString("reference"));
                        order.setCreationDatetime(rs.getTimestamp("creation_datetime"));
                        order.setTotalHT(rs.getDouble("total_ht"));
                        order.setTotalTTC(rs.getDouble("total_ttc"));

                        // Charger les lignes DishOrder (alias changé : do → d_order)
                        List<DishOrder> lines = new ArrayList<>();
                        String lineSql = """
                        SELECT d_order.id, d_order.id_order, d_order.id_dish, d_order.quantity, d.name
                        FROM dish_order d_order
                        JOIN dish d ON d.id = d_order.id_dish
                        WHERE d_order.id_order = ?
                        """;
                        try (PreparedStatement linePs = conn.prepareStatement(lineSql)) {
                            linePs.setInt(1, order.getId());
                            try (ResultSet lineRs = linePs.executeQuery()) {
                                while (lineRs.next()) {
                                    DishOrder doLine = new DishOrder();
                                    doLine.setId(lineRs.getInt("id"));
                                    doLine.setIdOrder(lineRs.getInt("id_order"));
                                    doLine.setIdDish(lineRs.getInt("id_dish"));
                                    doLine.setQuantity(lineRs.getInt("quantity"));

                                    Dish plat = new Dish();
                                    plat.setId(lineRs.getInt("id_dish"));
                                    plat.setName(lineRs.getString("name"));
                                    doLine.setDish(plat);

                                    lines.add(doLine);
                                }
                            }
                        }
                        order.setDishOrders(lines);
                        return order;
                    } else {
                        throw new RuntimeException("Commande introuvable avec référence : " + reference);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

