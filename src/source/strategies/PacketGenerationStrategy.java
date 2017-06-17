package source.strategies;

import source.Source;

public interface PacketGenerationStrategy {
    double getTimeToNextArrival(Source generator);
}
