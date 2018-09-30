package ff.ecochallenges.game;

public class Global {
    private static Global instance;

    // Global variable
    private int data;
    private String id;
    private String view="";

    // Restrict the constructor from being instantiated
    private Global(){}

    public void setData(int d){
        this.data=d;

    }
    public void setId(String d){
        this.id=d;
    }
    public void setView(String d){
        this.view =d;
    }
    public int getData(){
        return this.data;
    }
    public String getId(){
        return this.id;
    }
    public String getView(){return this.view;}

    public static synchronized Global getInstance(){
        if(instance==null){
            instance=new Global();
        }
        return instance;
    }
}
