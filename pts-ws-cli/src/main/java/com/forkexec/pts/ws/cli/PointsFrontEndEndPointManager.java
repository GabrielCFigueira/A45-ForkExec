package com.forkexec.pts.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.*;

import java.util.Collection;


public class PointsFrontEndEndPointManager {

	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming;

    private String uddiURL;

	/** constructor with provided UDDI location, WS name, and WS URL */
	public PointsFrontEndEndPointManager(String uddiURL) throws UDDINamingException {
        this.uddiURL = uddiURL;
        uddiNaming = new UDDINaming(uddiURL);
	}

    public String getUDDIUrl() {
        return uddiURL;
    }

    public Collection<UDDIRecord> listRecords(String orgName) throws UDDINamingException {
        return uddiNaming.listRecords(orgName);
    }
	

}