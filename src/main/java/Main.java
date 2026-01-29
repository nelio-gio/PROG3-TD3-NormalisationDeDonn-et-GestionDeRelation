import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();


        // PARTIE 4 : TESTS K3 - Gestion du TYPE et du STATUT de commande

        System.out.println("\n=== PARTIE 4 : TESTS K3 (TYPE ET STATUT DE COMMANDE) ===");

        // Test K3.1 : Création d'une commande avec TYPE = TAKE_AWAY et STATUT = CREATED par défaut
        System.out.println("Test K3.1 : Création commande TAKE_AWAY (statut CREATED par défaut)");
        Order cmdTakeAway = new Order();
        cmdTakeAway.setReference("ORD00006");
        cmdTakeAway.setTotalHT(8500.0);
        cmdTakeAway.setTotalTTC(9350.0);
        cmdTakeAway.setOrderType(OrderTypeEnum.TAKE_AWAY);
        cmdTakeAway.setCustomerName("Maman");

        DishOrder ligneTake = new DishOrder();
        ligneTake.setIdDish(1);
        ligneTake.setQuantity(2);
        cmdTakeAway.addDishOrder(ligneTake);

        try {
            Order savedTake = dr.saveOrder(cmdTakeAway);
            System.out.println("Sauvegarde OK :");
            System.out.println("  - Type : " + savedTake.getOrderType());
            System.out.println("  - Statut : " + savedTake.getOrderStatus());
            System.out.println("  - Client : " + savedTake.getCustomerName());
        } catch (Exception e) {
            System.out.println("Erreur K3.1 : " + e.getMessage());
        }

        // Test K3.2 : Vérification que le statut reste CREATED après sauvegarde
        System.out.println("\nTest K3.2 : Statut reste CREATED après sauvegarde");
        try {
            Order found = dr.findOrderByReference("ORD00006");
            System.out.println("Statut retrouvé : " + found.getOrderStatus());
        } catch (Exception e) {
            System.out.println("Erreur K3.2 : " + e.getMessage());
        }

        // Test K3.3 : Changement de statut vers READY (doit réussir)
        System.out.println("\nTest K3.3 : Passage à READY (doit réussir)");
        try {
            Order toReady = dr.findOrderByReference("ORD00006");
            toReady.setOrderType(OrderTypeEnum.EAT_IN); // Obligatoire pour saveOrder
            toReady.setOrderStatus(OrderStatusEnum.READY);
            Order updatedReady = dr.saveOrder(toReady);
            System.out.println("Mise à jour OK, nouveau statut : " + updatedReady.getOrderStatus());
        } catch (Exception e) {
            System.out.println("Erreur K3.3 : " + e.getMessage());
        }

        // Test K3.4 : Passage à DELIVERED puis tentative de modification (doit planter)
        System.out.println("\nTest K3.4 : Passage à DELIVERED puis tentative de modification (doit planter)");
        try {
            Order toDeliver = dr.findOrderByReference("ORD00006");
            toDeliver.setOrderType(OrderTypeEnum.TAKE_AWAY); // Obligatoire
            toDeliver.setOrderStatus(OrderStatusEnum.DELIVERED);
            Order delivered = dr.saveOrder(toDeliver);
            System.out.println("Livraison OK, statut : " + delivered.getOrderStatus());

            // Tentative de modification → doit planter
            delivered.setTotalHT(10000.0);
            dr.saveOrder(delivered);
            System.out.println("Erreur : modification acceptée après DELIVERED");
        } catch (IllegalStateException e) {
            System.out.println("Modification refusée (correct) : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Autre erreur K3.4 : " + e.getMessage());
        }

        System.out.println("\n=== FIN DE TOUS LES TESTS (TD3 + TD4 + ANNEXE + K3) ===");
    }
}