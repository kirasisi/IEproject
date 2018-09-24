package ff.ecochallenges.game;

public class Global {
    private static Global instance;

    // Global variable
    private int data;
    private int point;

    // Restrict the constructor from being instantiated
    private Global(){}

    public void setData(int d){
        this.data=d;

    }
    public void setpoint(int p){
        this.point=p;

    }
    public int getData(){
        return this.data;
    }
    public int getPoint(){
        return this.point;
    }

    public static synchronized Global getInstance(){
        if(instance==null){
            instance=new Global();
        }
        return instance;
    }
}
