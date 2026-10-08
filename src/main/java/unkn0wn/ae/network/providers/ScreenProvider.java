package unkn0wn.ae.network.providers;

import net.minecraft.nbt.NBTTagCompound;
import unkn0wn.ae.utils.ClientData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScreenProvider implements IClientDataProvider {

    public NBTTagCompound getData() {return null;}

    @Override
        public void setData(NBTTagCompound data){

        Calendar now = Calendar.getInstance();
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        Date date = new Date();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        try {
            ImageIO.write(screenShot, "PNG", new File(Paths.get("."+data.getString(ClientData.SCREEN.toString()))+"\\"+data.getString("name")+".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
