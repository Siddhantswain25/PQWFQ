import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private double spacingTime;
    private double serviceBitrate; //C[b/s]
    private boolean isBusy;

    public Server(double serviceBitrate) {
        this.spacingTime = 0.0;
        this.serviceBitrate = serviceBitrate;
    }

    public double getServiceBitrate() {
        return serviceBitrate;
    }

    public double getSpacingTime() {
        return spacingTime;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setSpacingTime(double spacingTime) {
        this.spacingTime = spacingTime;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }
}
