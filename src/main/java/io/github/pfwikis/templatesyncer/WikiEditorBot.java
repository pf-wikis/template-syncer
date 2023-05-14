package io.github.pfwikis.templatesyncer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.fastily.jwiki.core.Wiki;
import okhttp3.HttpUrl;

public class WikiEditorBot {

    public static void main(String[] args) throws IOException {
        var pf = new Wiki.Builder()
            .withDomain("https://pathfinderwiki.com")
            .withApiEndpoint(HttpUrl.get("https://pathfinderwiki.com/w/api.php"))
            .withLogin(args[0], args[1])
            .withDefaultLogger(false)
            .build();

        var pages = apiGET(pf, PageQuery.class, "query", "format", "json", "generator", "categorymembers", "utf8", "1", "formatversion", "2", "gcmtitle", "Category:Navboxes", "gcmprop", "ids", "gcmlimit", "1000").getPages();


        for(var page : pages) {
            String srcTxt = pf.getPageText(page.getTitle());
            String newTxt = srcTxt.replaceAll("\n *\\| *(imagestyle|imageleftstyle) *=.*", "");
            if(!srcTxt.equals(newTxt)) {
                System.out.println("Changed "+page.getTitle());
                pf.edit(page.getTitle(), newTxt, "Remove deprecated style parameters");
            }
        }
    }



    private static <T> T apiGET(Wiki wiki, Class<T> type, String action, String... params) throws IOException {
        var resp = wiki.basicGET(action, params);
        var bytes = resp.body().bytes();
        try {
            var response = Jackson.get().readValue(bytes, Response.class);
            return Jackson.get().treeToValue(response.getQuery(), type);
        } catch(Exception e) {
            System.err.println(new String(bytes));
            throw e;
        }
    }

    private static <T> T apiPOST(Wiki wiki, Class<T> type, String action, Map<String, String> params) throws IOException {
        var resp = wiki.basicPOST(action, new HashMap<>(params));
        var bytes = resp.body().bytes();
        try {
            var response = Jackson.get().readValue(bytes, Response.class);
            return Jackson.get().treeToValue(response.getQuery(), type);
        } catch(Exception e) {
            System.err.println(new String(bytes));
            throw e;
        }
    }
}
