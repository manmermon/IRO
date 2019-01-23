package example;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CustomEventPanel extends JPanel implements TimerListener {
  private int currentValue = 10;
    JProgressBar bar = new JProgressBar(1,100);
  public CustomEventPanel() {
    TimerComponent t = new TimerComponent(1000);
    t.addTimerListener(this);
    add(bar);
  }

  public void timeElapsed(TimerEvent evt) {
    currentValue += 10;
    bar.setValue(currentValue);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setTitle("Customized Event");
    frame.setSize(300, 80);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    Container contentPane = frame.getContentPane();
    contentPane.add(new CustomEventPanel());

    frame.show();
  }
}

interface TimerListener extends EventListener {
  public void timeElapsed(TimerEvent evt);
}

class TimerComponent extends Component implements Runnable {
  private int interval;

  private TimerListener listener;

  private static EventQueue evtq;

  public TimerComponent(int i) {
    interval = i;
    Thread t = new Thread(this);
    t.start();
    evtq = Toolkit.getDefaultToolkit().getSystemEventQueue();
    enableEvents(0);
  }

  public void addTimerListener(TimerListener l) {
    listener = l;
  }

  public void run() {
    while (true) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
      }
      TimerEvent te = new TimerEvent(this);
      evtq.postEvent(te);
    }
  }

  public void processEvent(AWTEvent evt) {
    if (evt instanceof TimerEvent) {
      if (listener != null)
        listener.timeElapsed((TimerEvent) evt);
    } else
      super.processEvent(evt);
  }

}

class TimerEvent extends AWTEvent {
  public static final int TIMER_EVENT = AWTEvent.RESERVED_ID_MAX + 5555;

  public TimerEvent(TimerComponent t) {
    super(t, TIMER_EVENT);
  }
}