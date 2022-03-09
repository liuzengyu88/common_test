package maintest;

import java.net.URL;

public class TryToResource {

    public static void main(String[] args) {

        String resPath = "exm.txt";

        URL resource = TryToResource.class.getClassLoader().getResource(resPath);

        System.out.println("resource = " + resource.toString());

    }

}
