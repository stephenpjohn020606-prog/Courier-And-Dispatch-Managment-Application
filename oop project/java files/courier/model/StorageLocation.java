package courier.model;

import courier.exceptions.ValidationException;

public class StorageLocation{
    private int Locationid;
    private String Section;
    private String Rack;
    private String Shelf;
    private boolean Isoccupied;
    private String Currentcourierid;

    public StorageLocation(String Section,String Rack,String Shelf) throws ValidationException{
        this(0,Section,Rack,Shelf,false,null);
    }
    public StorageLocation(int Locationid,String Section,String Rack,String Shelf,boolean Isoccupied,String Currentcourierid) throws ValidationException{
        this.Locationid=Locationid;
        Setsection(Section);
        Setrack(Rack);
        Setshelf(Shelf);
        this.Isoccupied=Isoccupied;
        this.Currentcourierid=Currentcourierid;
    }
    public int Getlocationid(){return Locationid;}
    public void Setlocationid(int Locationid){this.Locationid=Locationid;}
    public String Getsection(){return Section;}
    public void Setsection(String Section) throws ValidationException{
        if(Section==null||Section.trim().isEmpty()){
            throw new ValidationException("Storage section cannot be empty.");
        }
        this.Section=Section.trim();
    }
    public String Getrack(){return Rack;}
    public void Setrack(String Rack) throws ValidationException{
        if(Rack==null||Rack.trim().isEmpty()){
            throw new ValidationException("Storage rack cannot be empty.");
        }
        this.Rack=Rack.trim();
    }
    public String Getshelf(){return Shelf;}
    public void Setshelf(String Shelf) throws ValidationException{
        if(Shelf==null||Shelf.trim().isEmpty()){
            throw new ValidationException("Storage shelf cannot be empty.");
        }
        this.Shelf=Shelf.trim();
    }
    public boolean Isoccupied(){return Isoccupied;}
    public void Setoccupied(boolean Occupied){Isoccupied=Occupied;}
    public String Getcurrentcourierid(){return Currentcourierid;}
    public void Setcurrentcourierid(String Currentcourierid){this.Currentcourierid=Currentcourierid;}
    public String Getformattedcode(){
        return Section+" / "+Rack+" / "+Shelf;
    }
    @Override
    public String toString(){
        if(Isoccupied){
            return Getformattedcode()+" [Occupied by "+Currentcourierid+"]";
        }else{
            return Getformattedcode()+" [Available]";
        }
    }
}
