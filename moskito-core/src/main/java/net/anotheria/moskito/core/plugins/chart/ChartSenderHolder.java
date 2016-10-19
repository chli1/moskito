package net.anotheria.moskito.core.plugins.chart;

/**
 * Holder for {@link ChartSender}.
 */
public enum ChartSenderHolder {
    INSTANCE;

    private ChartSender chartSender = null;

    public void registerChartSender(ChartSender chartSender) {
        this.chartSender = chartSender;
    }

    public boolean isChartSenderRegistered() {
        return chartSender != null;
    }

    public ChartSender getChartSender() {
        return chartSender;
    }
}
