import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Le chef qui parle à la base : ajusté pour le nouveau pont, garde tes méthodes spéciales comme getNextSerialValue.
public class DataRetriever {
    Dish findDishById(Integer id) {
        try (Connection conn = new DBConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setSellingPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                dish.setDishIngredients(findDishIngredientsByDishId(id));  // Charge les cartes liens.
                return dish;
            }
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        List<DishIngredient> list = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    """
                    SELECT di.id, di.id_dish, di.id_ingredient, di.quantity_required, di.unit,
                           i.id AS ing_id, i.name AS ing_name, i.price AS ing_price, i.category AS ing_category
                    FROM dish_ingredient di
                    JOIN ingredient i ON di.id_ingredient = i.id
                    WHERE di.id_dish = ?
                    """);
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("ing_id"));
                ing.setName(rs.getString("ing_name"));
                ing.setPrice(rs.getDouble("ing_price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("ing_category")));

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setIdDish(rs.getInt("id_dish"));
                di.setIdIngredient(rs.getInt("id_ingredient"));
                di.setQuantityRequired(rs.getDouble("quantity_required"));
                di.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                di.setIngredient(ing);
                list.add(di);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

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

            deleteDishIngredients(conn, dishId);
            insertDishIngredients(conn, dishId, toSave.getDishIngredients());

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dish_ingredient WHERE id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients) throws SQLException {
        if (dishIngredients == null || dishIngredients.isEmpty()) return;
        String sql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIdIngredient());
                ps.setDouble(3, di.getQuantityRequired());
                ps.setString(4, di.getUnit().name());
                ps.addBatch();  // Comme envoyer plusieurs lettres en une fois pour vitesse.
            }
            ps.executeBatch();
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return List.of();
        List<Ingredient> saved = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            String sql = """
                    INSERT INTO ingredient (id, name, category, price)
                    VALUES (?, ?, ?::ingredient_category, ?)
                    RETURNING id
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Ingredient ing : newIngredients) {
                    if (ing.getId() != null) {
                        ps.setInt(1, ing.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ing.getName());
                    ps.setString(3, ing.getCategory().name());
                    ps.setDouble(4, ing.getPrice());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ing.setId(rs.getInt(1));
                        saved.add(ing);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

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
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException("No sequence found for " + tableName + "." + columnName);
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
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}