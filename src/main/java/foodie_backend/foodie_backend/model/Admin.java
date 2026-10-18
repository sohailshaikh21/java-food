package foodie_backend.foodie_backend.model;

public class Admin extends Person {

    public Admin() {
    }

    public Admin(String name, String phone, String email) {
        super(name, phone, email);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String describe() {
        return "Admin " + getName();
    }
}
