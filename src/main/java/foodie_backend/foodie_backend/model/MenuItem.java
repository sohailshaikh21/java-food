package foodie_backend.foodie_backend.model;

public class MenuItem {

    private Long id;
    private String name;
    private String category;
    private int price;
    private String foodType;
    private String imageUrl;
    private String description;
    private boolean available;

    public MenuItem() {
    }

    public MenuItem(String name, String category, int price, String foodType) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.foodType = foodType;
        this.available = true;
    }

    public MenuItem(String name, String category, int price, String foodType,
                    String imageUrl, String description, boolean available) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.foodType = foodType;
        this.imageUrl = imageUrl;
        this.description = description;
        this.available = available;
    }

    public MenuItem(Long id, String name, String category, int price, String foodType,
                    String imageUrl, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.foodType = foodType;
        this.imageUrl = imageUrl;
        this.description = description;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
