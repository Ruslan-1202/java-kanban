package Tasks;
import Enums.*;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String descr, Status status, int id, int epicId) {
        super(name, descr, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
