package us.juggl.twentyseventeen.february;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.spi.Registry;

/**
 * A Camel Java8 DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    public static final String SEDA_OPTS = "?multipleConsumers=true&concurrentConsumers=5";

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        StringBuilder uriBuilder = new StringBuilder("twitter://streaming/filter?");
        uriBuilder.append("type=event&");
        uriBuilder.append("lang=en&");
        Registry reg = getContext().getRegistry();
        uriBuilder.append("consumerKey=").append(reg.lookupByName("api.key")).append("&");
        uriBuilder.append("consumerSecret=").append(reg.lookupByName("api.secret")).append("&");
        uriBuilder.append("accessToken=").append(reg.lookupByName("access.token")).append("&");
        uriBuilder.append("accessTokenSecret=").append(reg.lookupByName("access.secret"));
        String baseUri = uriBuilder.toString();

        //String keywords = "#HogwartsRunningClub,#GeeksForGood,#HRCStrong,#HRCHustlePuff17,#HRCRacenclaw17,#HRCSlytheRun17,#HRCGryffinDash17,#HRCFacultyBeasts17,#somuchgood";
        String keywords = "#grammys";

        ValueBuilder query = constant("INSERT INTO tweets (id, status, created, screenname) VALUES (:?id, :?status, :?created, :?screenname)");
        from("seda:tweet2db" + SEDA_OPTS + "&size=" + reg.lookupByName("db.pool"))
                .setHeader("id", simple("${body.getId()}"))
                .setHeader("status", simple("${body.getText()}"))
                .setHeader("created", simple("${body.getCreatedAt().getTime()}"))
                .setHeader("screenname", simple("${body.getUser().getScreenName()}"))
                .setBody(query)
                .to("jdbc:lykely?useHeadersAsParameters=true");

        from("seda:tweet2log" + SEDA_OPTS).sample(10).log(simple("${body.getText()}").getText());

        from(baseUri+"&keywords="+keywords)
            .filter(simple("${body.isRetweet()} == false"))
            .multicast().to("seda:tweet2db", "seda:tweet2log");
    }
}
