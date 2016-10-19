package net.anotheria.moskito.extensions;

import net.anotheria.moskito.core.plugins.AbstractMoskitoPlugin;
import net.anotheria.moskito.core.plugins.chart.ChartSenderHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MailGun chart sender plugin.
 */
public class MailgunChartSenderPlugin extends AbstractMoskitoPlugin {

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MailgunChartSenderPlugin.class);


    @Override
    public void initialize() {
        ChartSenderHolder.INSTANCE.registerChartSender(new MailgunChartSender());
    }

    @Override
    public void deInitialize() {
        ChartSenderHolder.INSTANCE.registerChartSender(null);
        super.deInitialize();
    }

}
