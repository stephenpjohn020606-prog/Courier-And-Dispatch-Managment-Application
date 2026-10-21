package courier.model;

import courier.exceptions.InvalidStatusTransitionException;

public interface Dispatchable{
    boolean Canbedispatched();
    void Recorddispatch(DispatchRecord Record) throws InvalidStatusTransitionException;
    DispatchRecord Getdispatchrecord();
}
