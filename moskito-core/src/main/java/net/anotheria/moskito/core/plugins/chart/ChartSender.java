package net.anotheria.moskito.core.plugins.chart;

/**
 * Interface that has to be implemented to provide chart sender.
 */
public interface ChartSender {

    /**
     * Send chart to all recipients in list
     *
     * @param filename chart file name
     * @param image64 image as byte array
     * @param subject subject of message
     * @param recipients recipients list
     */
    void sendChart(String filename, String image64, String subject, String[] recipients);
}
