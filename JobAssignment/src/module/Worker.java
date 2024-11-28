package module;

public class Worker extends People{
    private String position;
    private int salary;

    public Worker() {
    }
    public Worker(int id, String name){
        super(id, name);
    }

    public Worker(int id, String name, String phoneNumber, byte age, String address, String position, int salary) {
        super(id, name, phoneNumber, age, address);
        this.position = position;
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", age=" + getAge() +
                ", address='" + getAddress() + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                '}';
    }
}
