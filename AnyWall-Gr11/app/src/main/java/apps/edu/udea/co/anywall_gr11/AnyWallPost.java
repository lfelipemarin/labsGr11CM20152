package apps.edu.udea.co.anywall_gr11;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by felipe on 29/11/15.
 */
@ParseClassName("Posts")
public class AnyWallPost extends ParseObject {
    public String getText() {

        return getString("text");
    }

    public void setText(String value) {

        put("text", value);
    }

    public ParseUser getUser() {

        return getParseUser("user");
    }

    public void setUser(String value) {

        put("user", value);
    }

    public ParseGeoPoint getLocation() {

        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {

        put("location", value);
    }

    public static ParseQuery<AnyWallPost> getQuery() {
        return ParseQuery.getQuery(AnyWallPost.class);
    }
}
