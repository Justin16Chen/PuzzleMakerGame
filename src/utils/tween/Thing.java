package utils.tween;

public class Thing {
    public Thing() {
        Integer num = 5;
        Timer.createCallTimer("call thing", this, 1, "functionThing", "ddddd", num);
        Updatable.updateUpdatables(1);
    }
    private void functionThing(String param, int num) {
        System.out.println("called thing with " + param + " " + num);
    }

    //public static void main(String[] args) {
    //    new Thing();
    //}
}