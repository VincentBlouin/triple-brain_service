/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WikidataSearchResultForProjectScenario implements JsTestScenario {

    @Override
    public JSONObject build() {
        try {
            return new JSONObject(
                    "{\"searchinfo\":{\"search\":\"project\"},\"search\":[{\"id\":\"Q170584\",\"concepturi\":\"http://www.wikidata.org/entity/Q170584\",\"url\":\"//www.wikidata.org/wiki/Q170584\",\"title\":\"Q170584\",\"pageid\":170848,\"label\":\"project\",\"description\":\"collaborative enterprise, frequently involving research or design, that is carefully planned to achieve a particular aim\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"project\"}},{\"id\":\"Q526384\",\"concepturi\":\"http://www.wikidata.org/entity/Q526384\",\"url\":\"//www.wikidata.org/wiki/Q526384\",\"title\":\"Q526384\",\"pageid\":495363,\"label\":\"Project\",\"description\":\"Wikipedia disambiguation page\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"Project\"}},{\"id\":\"Q428051\",\"concepturi\":\"http://www.wikidata.org/entity/Q428051\",\"url\":\"//www.wikidata.org/wiki/Q428051\",\"title\":\"Q428051\",\"pageid\":403888,\"label\":\"Project\",\"description\":\"Music album\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"Project\"}},{\"id\":\"Q20891703\",\"concepturi\":\"http://www.wikidata.org/entity/Q20891703\",\"url\":\"//www.wikidata.org/wiki/Q20891703\",\"title\":\"Q20891703\",\"pageid\":22694310,\"label\":\"Project\",\"description\":\"painting by Ben Nicholson\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"Project\"}},{\"id\":\"Q4039395\",\"concepturi\":\"http://www.wikidata.org/entity/Q4039395\",\"url\":\"//www.wikidata.org/wiki/Q4039395\",\"title\":\"Q4039395\",\"pageid\":3849542,\"label\":\"Project:Administrators\",\"description\":\"project page explaining the rights of and policies pertaining to the users tasked with project maintenance\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"Project:Administrators\"}},{\"id\":\"Q3938\",\"concepturi\":\"http://www.wikidata.org/entity/Q3938\",\"url\":\"//www.wikidata.org/wiki/Q3938\",\"title\":\"Q3938\",\"pageid\":4849,\"label\":\"Sandbox\",\"description\":\"project page for making test edits (please use Wikidata's own sandbox or Q4115189 for Wikidata test edits)\",\"match\":{\"type\":\"alias\",\"language\":\"en\",\"text\":\"Project:Sandbox\"},\"aliases\":[\"Project:Sandbox\"]},{\"id\":\"Q16503\",\"concepturi\":\"http://www.wikidata.org/entity/Q16503\",\"url\":\"//www.wikidata.org/wiki/Q16503\",\"title\":\"Q16503\",\"pageid\":19219,\"label\":\"Project:Village pump\",\"description\":\"set of pages used to discuss the technical issues, policies, and operations of Wikimedia\",\"match\":{\"type\":\"label\",\"language\":\"en\",\"text\":\"Project:Village pump\"}}],\"search-continue\":7,\"success\":1}"
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
