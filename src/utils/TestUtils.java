package utils;

public class TestUtils {
    //задержка, чтобы создавались задачи без явного задания времени
    public static void sleepTask() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("поломался слипер");
        }
    }
}
