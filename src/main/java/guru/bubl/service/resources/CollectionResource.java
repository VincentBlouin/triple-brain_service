package guru.bubl.service.resources;

import com.google.common.reflect.TypeToken;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.json.JsonUtils;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public interface CollectionResource {
    String getUsername();

    default Boolean areAllUrisOwned(Set<URI> uris) {
        return uris.stream().allMatch(
                uri -> UserUris.ownerUserNameFromUri(uri).equals(getUsername())
        );
    }

    default Set<URI> urisFromJsonArray(String jsonArray) {
        return JsonUtils.getGson().fromJson(jsonArray, new TypeToken<HashSet<URI>>() {
        }.getType());
    }
}
