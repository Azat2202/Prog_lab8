package dtp;

public record User(String name, String password) {
    @Override
    public String toString() {
        return name();
    }
}
