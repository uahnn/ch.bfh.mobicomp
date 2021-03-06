/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.smarthome.remoteSwitch.message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Reto E. Koenig <reto.koenig@bfh.ch>
 */
public class RemoteSwitchEncoder implements Encoder.Text<RemoteSwitch> {

    @Override
    public String encode(RemoteSwitch remoteSwitch) throws EncodeException {
	return remoteSwitch.getJson().toString();
    }

    @Override
    public void init(EndpointConfig config) {
	System.out.println("init");
    }

    @Override
    public void destroy() {
	System.out.println("destroy");
    }

}
