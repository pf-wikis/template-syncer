package io.github.pfwikis.templatesyncer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.fastily.jwiki.core.Wiki;
import okhttp3.HttpUrl;

public class Syncer {

    public static void main(String[] args) throws IOException {
        var pf = new Wiki.Builder()
            .withDomain("https://pathfinderwiki.com")
            .withApiEndpoint(HttpUrl.get("https://pathfinderwiki.com/w/api.php"))
            .withLogin(args[0], args[1])
            .withDefaultLogger(false)
            .build();
        var sf = new Wiki.Builder()
            .withDomain("https://starfinderwiki.com")
            .withApiEndpoint(HttpUrl.get("https://starfinderwiki.com/w/api.php"))
            .withLogin(args[0], args[1])
            .withDefaultLogger(false)
            .build();

        var token = apiGET(sf, TokenQuery.class,"query", "format", "json" , "meta", "tokens", "utf8","1","formatversion","2");
        var pages = apiGET(pf, PageQuery.class, "query", "format", "json", "generator", "categorymembers", "utf8", "1", "formatversion", "2", "gcmtitle", "Category:Synced to starfinderwiki", "gcmprop", "ids", "gcmnamespace", "10|102|274", "gcmlimit", "1000").getPages();


        for(var page : pages) {
            sync(pf, sf, page.getTitle(), token);

            String doc = page.getTitle()+"/doc";
            if(pf.exists(doc)) {
                sync(pf, sf, doc, token);
            }
        }
    }

    private static void sync(Wiki pf, Wiki sf, String page, TokenQuery token) throws IOException {
        var target = pf.getPageText(page);
        var sfText = sf.getPageText(page);
        target = "<noinclude><div class=\"banner\">"
            + "This page is automatically synced from [https://pathfinderwiki.com/wiki/{{FULLPAGENAMEE}} this] pathfinderwiki page. "
            + "Do not edit it here.</div></noinclude>"
            + target;

        if(!sfText.equals(target)) {
            sf.edit(page, target, "Autosync from Pathfinderwiki");
            apiPOST(sf, JsonNode.class, "protect", Map.of("format", "json", "title", page, "protections", "edit=sysop|move=sysop", "reason", "Automatically synced from pathfinderwiki", "token", token.getTokens().getCsrftoken(), "utf8", "1", "formatversion", "2"));
            System.out.println("Synced&Protected "+page+" to Starfinderwiki");
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
