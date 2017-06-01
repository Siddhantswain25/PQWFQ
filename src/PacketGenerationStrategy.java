public interface PacketGenerationStrategy {
    double getTimeToNextArrival(Source generator);
}
