package us.juggl.twentyseventeen.february;

import org.apache.camel.main.Main;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * A Camel Application
 */
public class MainApp {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    private static Properties props = new Properties();

    private MainApp() {
        super();
    }

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        try {
            props.load(MainApp.class.getResourceAsStream("/twitter.properties"));
        } catch (IOException ioe) {
            LOG.error("Unable to load Twitter properties", ioe);
            return;
        }

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(props.getProperty("db.url"));
        ds.setUsername(props.getProperty("db.user"));
        ds.setPassword(props.getProperty("db.pass"));
        try {
            ds.setMaxTotal(Integer.parseInt(props.getProperty("db.pool")));
        } catch (NumberFormatException nfe) {
            LOG.error("Unable to parse '%s' as an integer", props.getProperty("db.pool"));
            ds.setMaxTotal(20);
        }

        Main main = new Main();
        main.bind("lykely", ds);
        props.entrySet().forEach(e -> main.bind((String)e.getKey(), e.getValue()) );
        main.addRouteBuilder(new MyRouteBuilder());
        main.run(args);
    }
}

