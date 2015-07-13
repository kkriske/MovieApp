/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author kristof
 */
public class StorageDevice {

    private File rootDirectory;
    private String deviceName;

    public StorageDevice(File rootDirectory, String deviceName) {
        if (rootDirectory == null || !rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid root file!");
        }

        this.rootDirectory = rootDirectory;

        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = rootDirectory.getName();
        }

        this.deviceName = deviceName;
    }

    public StorageDevice(File rootDirectory) {
        this(rootDirectory, null);
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    /**
     *
     * @return the name of the USB storage device
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Check if it is possible to read in this device.
     *
     * @see File#canRead()
     *
     * @return <b>true</b> if it is possible to perform read operations in this
     * device, <b>false</b> otherwise.
     */
    public boolean canRead() {
        return rootDirectory.canRead();
    }

    /**
     * Check if it is possible to write in this device.
     *
     * @see File#canWrite()
     *
     * @return <b>true</b> if it is possible to perform write operations in this
     * device, <b>false</b> otherwise.
     */
    public boolean canWrite() {
        return rootDirectory.canWrite();
    }

    /**
     * Check if the actual user has execute permissions in this drive.
     *
     * @see File#canWrite()
     *
     * @return <b>true</b> if it is possible to perform execute operations in
     * this device, <b>false</b> otherwise.
     */
    public boolean canExecute() {
        return rootDirectory.canExecute();
    }

    /**
     * @see FileSystemView#getSystemDisplayName(java.io.File)
     *
     * @return the name of the root of this device as it would be displayed in a
     * system file browser.
     */
    public String getSystemDisplayName() {
        return FileSystemView.getFileSystemView().getSystemDisplayName(rootDirectory);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.rootDirectory != null ? this.rootDirectory.hashCode() : 0);
        hash = 89 * hash + (this.deviceName != null ? this.deviceName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StorageDevice other = (StorageDevice) obj;
        return this.rootDirectory == other.rootDirectory || (this.rootDirectory != null && this.rootDirectory.equals(other.rootDirectory));
    }

    @Override
    public String toString() {
        return String.format("RemovableDevice [Root=%s, Device Name=%s]", rootDirectory, deviceName);
    }
}
