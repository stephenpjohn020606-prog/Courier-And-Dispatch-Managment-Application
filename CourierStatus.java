package courier.model;

public enum CourierStatus{
    STORED("Stored"),
    READY_FOR_DISPATCH("Ready for Dispatch"),
    DISPATCHED("Dispatched"),
    DELIVERED("Delivered");

    private final String Displayname;

    CourierStatus(String Displayname){
        this.Displayname=Displayname;
    }
    public String Getdisplayname(){
        return Displayname;
    }
    public boolean Cantransitionto(CourierStatus Next){
        if(this==Next)return true;
        if(this==STORED){
            return Next==READY_FOR_DISPATCH;
        }else if(this==READY_FOR_DISPATCH){
            return Next==DISPATCHED;
        }else if(this==DISPATCHED){
            return Next==DELIVERED;
        }else{
            return false;
        }
    }
    public static CourierStatus Fromstring(String Text){
        if(Text==null)return STORED;
        for(CourierStatus S:values()){
            if(S.Displayname.equalsIgnoreCase(Text.trim())||S.name().equalsIgnoreCase(Text.trim())){
                return S;
            }
        }
        return STORED;
    }
    @Override
    public String toString(){
        return Displayname;
    }
}
