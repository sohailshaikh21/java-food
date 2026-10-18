package foodie_backend.foodie_backend.service;

import foodie_backend.foodie_backend.model.MenuItem;
import foodie_backend.foodie_backend.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getFullMenu() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAvailableMenu() {
        return menuItemRepository.findAll().stream()
                .filter(MenuItem::isAvailable)
                .toList();
    }
}
