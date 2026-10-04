package playground;

/**
 * A wire from the output of one component to one input port of another.
 * Independent of how the UI draws it.
 */
public record Connection(String sourceId, String targetId, int targetPort) {
}
