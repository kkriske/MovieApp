/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kristof
 */
public class StorageDeviceDetector {

    private static StorageDeviceDetector detector;

    public static StorageDeviceDetector getInstance() {
        if (detector == null) {
            detector = new StorageDeviceDetector();
        }
        return detector;
    }

    /**
     * wmic logicaldisk where drivetype=2 get description,deviceid,volumename
     */
    private static final String windowsDetectUSBCommand = "wmic logicaldisk where drivetype=2 get deviceid";

    private StorageDeviceDetector() {
    }

    public List<StorageDevice> getRemovableDevices() {
        ArrayList<StorageDevice> listDevices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Runtime.getRuntime().exec(windowsDetectUSBCommand).getInputStream()))) {

            String outputLine = reader.readLine();
            while (outputLine != null) {
                outputLine = outputLine.trim();

                if (!outputLine.isEmpty() && !"DeviceID".equals(outputLine)) {
                    listDevices.add(new StorageDevice(new File(outputLine + File.separatorChar)));
                }

                outputLine = reader.readLine();
            }

        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }

        return listDevices;
    }
}
