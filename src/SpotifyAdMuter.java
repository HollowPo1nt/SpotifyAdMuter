import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class SpotifyAdMuter {

    public static void main(String[] args) {

        // copy nircmd.exe from jar so that it can be used in the program
        // to operate system volume
        String homeDir = System.getProperty("user.home");
        File nirDirectory = new File(homeDir + "/Downloads/nircmd-x64");

        if (!nirDirectory.exists()) {
            copyExe(homeDir, nirDirectory);
        }

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = null;

            try {
                image = ImageIO.read(SpotifyAdMuter.class.getResourceAsStream("trayIcon.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create a popup menu
            final PopupMenu popup = new PopupMenu();

            // construct a TrayIcon
            final TrayIcon trayIcon = new TrayIcon(image, "SpotifyAdMuter", popup);

            ActionListener muteAd = e -> {
                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(homeDir + "\\Downloads\\nircmd-x64\\nircmd.exe" + " mutesysvolume 1");
                    System.out.println("system volume muted");
                    trayIcon.displayMessage("SpotifyAdMuter", "System volume muted", TrayIcon.MessageType.INFO);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Runtime rt = Runtime.getRuntime();
                            rt.exec(homeDir + "\\Downloads\\nircmd-x64\\nircmd.exe" + " mutesysvolume 0");
                            System.out.println("system volume unmuted");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };

                timer.schedule(task, 30000);
            };

            ActionListener closeListener = e -> System.exit(0);

            // create menu item for the default action
            MenuItem defaultItem = new MenuItem("Mute ad");
            defaultItem.addActionListener(muteAd);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);

            popup.add(defaultItem);
            popup.add(closeItem);

            // set the TrayIcon properties
            trayIcon.addActionListener(muteAd);

            // add the tray image
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    static void copyExe(String home, File file) {
        URL url = SpotifyAdMuter.class.getClassLoader().getResource("nircmd.exe");

        try {
            file.mkdirs();
            file = new File(file.getAbsoluteFile() + "/nircmd.exe");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream input = url.openStream();
             FileOutputStream output = new FileOutputStream(home + "/Downloads/nircmd-x64/nircmd.exe")) {
            byte[] buffer = new byte[119300];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
