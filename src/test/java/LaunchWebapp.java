/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

import guru.bubl.service.Launch;
import guru.bubl.service.Launcher;

public class LaunchWebapp {

    public static void main(String[] args) throws Exception {
        Launch.before();
        Launcher launcher = new Launcher();
        launcher.launch();
        Launch.after();
    }
}
