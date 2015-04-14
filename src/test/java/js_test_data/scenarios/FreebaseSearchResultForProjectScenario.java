/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class FreebaseSearchResultForProjectScenario implements JsTestScenario {

    @Override
    public JSONObject build() {
        try {
            return new JSONObject(
                    "{'status':'200 OK','result':[{'mid':'/m/05bh61d','id':'/projects/project','name':'Project','lang':'en','score':86.719795},{'mid':'/m/05bh614','id':'/projects/project_focus','name':'Project focus','lang':'en','score':78.006828},{'mid':'/m/05bh64w','id':'/projects/project_participation','name':'Project participation','lang':'en','score':63.436409},{'mid':'/m/05bh63t','id':'/projects/project/project_focus','name':'Project focus','lang':'en','score':61.986198},{'mid':'/m/05bh60n','id':'/projects/project_participant','name':'Project participant','lang':'en','score':60.379417},{'mid':'/m/05bh60x','id':'/projects/project_role','name':'Project role','lang':'en','score':40.751884},{'mid':'/m/02hrl4k','id':'/architecture/landscape_project','name':'Landscape project','lang':'en','score':33.332333},{'mid':'/m/02hs3pj','id':'/language/human_language/rosetta_project_code','name':'Rosetta Project Code','lang':'en','score':32.797436},{'mid':'/m/03ywhr0','id':'/astronomy/astronomical_survey_project_organization','name':'Astronomical Survey/Project Organization','lang':'en','score':23.093378},{'mid':'/m/0kpv4b','id':'/music/recording_contribution/contributor','name':'Contributor','lang':'en','score':17.955832},{'mid':'/m/0kpv4m','id':'/music/recording_contribution/performance_role','name':'Role','lang':'en','score':16.361845},{'mid':'/m/02hrl4z','id':'/architecture/landscape_architect/landscape_project','name':'Landscape Projects','lang':'en','score':14.924665},{'mid':'/m/02yx1ps','id':'/user/bio2rdf/project','name':'Project','lang':'en','score':13.223398},{'mid':'/m/051qp2b','id':'/base/centreforeresearch/project','name':'Project','lang':'en','score':11.577896},{'mid':'/m/09k3bjl','id':'/base/bibkn/author/mgp_id','name':'Mathematics Genealogy Project Id','lang':'en','score':11.095313},{'mid':'/m/071_x2b','id':'/base/engineering/engineering_project','name':'Engineering Project','lang':'en','score':10.326856},{'mid':'/m/01xpmks','id':'/architecture/structure/construction_started','name':'Construction Started','lang':'en','score':10.320779},{'mid':'/m/01xpml8','id':'/architecture/structure/contractor','name':'Contractor','lang':'en','score':10.223261},{'mid':'/m/04j76p7','id':'/user/paulshanks/project_management','name':'Project Management','lang':'en','score':9.041636},{'mid':'/m/0455q2p','id':'/user/sandos/research/research_project','name':'Research project','lang':'en','score':8.272524}],'cursor':20,'cost':17,'hits':88195}"
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
