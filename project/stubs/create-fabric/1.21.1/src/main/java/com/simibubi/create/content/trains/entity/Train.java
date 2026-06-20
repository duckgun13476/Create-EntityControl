package com.simibubi.create.content.trains.entity;

import java.util.ArrayList;
import java.util.List;

public class Train {
    public Object id;
    public double speed;
    public double targetSpeed;
    public double throttle;
    public double speedBeforeStall;
    public boolean manualTick;
    public TravellingPoint.SteerDirection manualSteer = TravellingPoint.SteerDirection.NONE;
    public Object backwardsDriver;
    public List<Carriage> carriages = new ArrayList<>();
}
