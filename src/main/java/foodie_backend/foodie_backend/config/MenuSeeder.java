package foodie_backend.foodie_backend.config;

import foodie_backend.foodie_backend.model.MenuItem;
import foodie_backend.foodie_backend.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MenuSeeder.class);

    private final MenuItemRepository menuItemRepository;

    public MenuSeeder(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) {
        if (menuItemRepository.count() > 0) {
            return;
        }

        seed().forEach(menuItemRepository::save);
        log.info("Seeded {} menu items", menuItemRepository.count());
    }

    private List<MenuItem> seed() {
        return List.of(
                new MenuItem("Margherita Pizza", "Pizza", 199, "Veg", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=700&q=80", "Classic tomato, mozzarella and basil pizza.", true),
                new MenuItem("Pepperoni Pizza", "Pizza", 249, "Non-Veg", "https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=700&q=80", "Loaded with pepperoni and melted cheese.", true),
                new MenuItem("Chicken Burger", "Burgers", 149, "Non-Veg", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=700&q=80", "Juicy chicken patty with fresh vegetables.", true),
                new MenuItem("Classic Veg Burger", "Burgers", 129, "Veg", "https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=700&q=80", "Crispy veggie patty with creamy sauce.", true),
                new MenuItem("Pasta Alfredo", "Pasta", 179, "Veg", "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?auto=format&fit=crop&w=700&q=80", "Creamy white sauce pasta with herbs.", true),
                new MenuItem("Chicken Pasta", "Pasta", 199, "Non-Veg", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=700&q=80", "Pasta with tender chicken and rich sauce.", true),
                new MenuItem("Paneer Tikka", "Main Course", 229, "Veg", "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&w=700&q=80", "Grilled paneer with Indian spices.", true),
                new MenuItem("Chicken Biryani", "Main Course", 249, "Non-Veg", "https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=700&q=80", "Aromatic basmati rice with juicy chicken.", true),
                new MenuItem("French Fries", "Starters", 99, "Veg", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=700&q=80", "Crispy golden fries with seasoning.", true),
                new MenuItem("Garlic Bread", "Starters", 89, "Veg", "https://images.unsplash.com/photo-1619535860434-ba1d8fa12536?auto=format&fit=crop&w=700&q=80", "Buttery garlic bread with herbs.", true),
                new MenuItem("Chocolate Lava Cake", "Desserts", 129, "Veg", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=700&q=80", "Warm chocolate cake with molten center.", true),
                new MenuItem("Cheesecake", "Desserts", 149, "Veg", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?auto=format&fit=crop&w=700&q=80", "Creamy cheesecake with fresh topping.", true),
                new MenuItem("Cold Coffee", "Beverages", 99, "Veg", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=700&q=80", "Chilled creamy coffee with ice.", true),
                new MenuItem("Fresh Lime Soda", "Beverages", 79, "Veg", "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=700&q=80", "Refreshing lime drink served chilled.", true));
    }
}
