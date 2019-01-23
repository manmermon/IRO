package example;

import java.util.ArrayList;
import java.util.List;

interface HelloListener {
    void someoneSaidHello();
}

public class eventExample {
	
	 public static void main(String[] args) {
	    	event initiater = new event();
	        Responder responder = new Responder();
	        initiater.addListener(responder);
	        initiater.sayHello();  // "Hello!" and "Hello there!"
	    }

	
	
}

class Responder implements HelloListener {
    @Override
    public void someoneSaidHello() {
        System.out.println("Hello there!");
    }
}

class event {
	private List<HelloListener> listeners = new ArrayList<HelloListener>();

    public void addListener(HelloListener toAdd) {
        listeners.add(toAdd);
    }

    public void sayHello() {
        System.out.println("Hello!");

        // Notify everybody that may be interested.
        for (HelloListener hl : listeners)
            hl.someoneSaidHello();
    }
}
