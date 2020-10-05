package com.example.movieapplication;
import androidx.appcompat.widget.ActivityChooserView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddress {
    public String get_ipaddress () {
        InetAddress inetAddress;
        String host_name = "";
        try{
            inetAddress = InetAddress.getLocalHost();
            host_name = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return host_name;
    }

}
