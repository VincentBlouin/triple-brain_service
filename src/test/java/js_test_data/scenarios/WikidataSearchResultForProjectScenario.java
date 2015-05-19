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
                    "{'warnings':{'main':{'*':'Unrecognized parameter: \"_\"'}},'searchinfo':{'search':'project'},'search':[{'id':'Q170584','url':'//www.wikidata.org/wiki/Q170584','description':'collaborative enterprise, frequently involving research or design, that is carefully planned to achieve a particular aim','label':'project'},{'id':'Q526384','url':'//www.wikidata.org/wiki/Q526384','description':'Wikipedia disambiguation page','label':'Project'},{'id':'Q428051','url':'//www.wikidata.org/wiki/Q428051','description':'Music album','label':'Project'},{'id':'Q4039395','url':'//www.wikidata.org/wiki/Q4039395','description':'project page explaining the rights of and policies pertaining to the users tasked with project maintenance','label':'Project:Administrators'},{'id':'Q3938','url':'//www.wikidata.org/wiki/Q3938','aliases':['Project:Sandbox'],'description':'project page for making test edits (please use Wikidata\"s own sandbox or Q4115189 for Wikidata test edits)','label':'Sandbox'},{'id':'Q16503','url':'//www.wikidata.org/wiki/Q16503','description':'set of pages used to discuss the technical issues, policies, and operations of Wikimedia','label':'Project:Village pump'},{'id':'Q4980478','url':'//www.wikidata.org/wiki/Q4980478','description':'general disclaimer of a Wikimedia project','label':'Project:General disclaimer'}],'search-continue':7,'success':1}"
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
