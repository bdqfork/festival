package cn.bdqfork.example.ioc;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
