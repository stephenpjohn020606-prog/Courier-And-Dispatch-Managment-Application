package courier.model;

import courier.exceptions.InvalidStorageLocationException;

public interface Storable{
    boolean Hasassignedstorage();
    void Assignstoragelocation(StorageLocation Location) throws InvalidStorageLocationException;
    void Clearstoragelocation();
    StorageLocation Getstoragelocation();
}
