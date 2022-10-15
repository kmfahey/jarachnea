package jarachnea;

import jarachnea.Handle;

public class Debugger {

    public static void main(String[] args) {
        Handle handleObj = new Handle("gargron", "mastodon.social");
        System.out.println(handleObj.toHandle());
    }
}
