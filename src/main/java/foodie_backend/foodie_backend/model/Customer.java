package foodie_backend.foodie_backend.model;

public class Customer extends Person {

    private String address;

    public Customer() {
    }

    public Customer(String name, String phone, String email) {
        super(name, phone, email);
    }

    public Customer(Long id, String name, String phone, String email) {
        super(id, name, phone, email);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public String describe() {
        return "Customer " + getName();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
