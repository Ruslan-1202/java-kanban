package Tasks;/* Из ТЗ:
Для эталонного решения мы выбрали создание публичного не абстрактного класса Tasks.Task,
который представляет отдельно стоящую задачу. Его данные наследуют подклассы Subtask и Tasks.Epic.
В нашем задании класс Tasks.Task можно использовать сам по себе, не делая его абстрактным.
*/

import java.util.Objects;
import Enums.*;

public class Task {

    protected String name;
    protected String descr;
    protected final int id;
    protected Status status;

    public Task(String name, String descr, Status status, int id) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.descr = descr;
    }

    public int getId() {
        return id;
    }

    public Status getStaus() {
        return status;
    }

    public void setStaus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.getClass() + "{" +
                "name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(id);
    }
}
