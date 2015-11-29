package apps.edu.udea.co.anywall_gr11;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by felipe on 27/11/15.
 */
public class Aplicacion extends android.app.Application {
    public static final boolean APPDEBUG = false;

    public static final String APPTAG = "AnyWall";

    public Aplicacion() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(AnyWallPost.class);
        Parse.initialize(this, "wyw0BXwj7PMVQ8YjR7Mx7BRwfoHkoxOjWJu8AGz5",
                "B5VIt4YkCARkXSvnmI8bRLIgOzxus4A1jyzUcpFZ");

        ParseUser.enableAutomaticUser();
        ParseACL defaultAcl = new ParseACL();
        defaultAcl.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultAcl, true);
    }
}

